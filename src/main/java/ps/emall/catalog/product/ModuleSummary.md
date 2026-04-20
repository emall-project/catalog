### 1. Domain Model Summary
- **Main Entity:** `Product` represents a catalog item.
- **Hierarchy & Ownership:** A product belongs strictly to one `Category`, one `Brand`, and is owned by a specific `Store` (`storeId`) within a `Mall` (`mallId`).
- **Composition & Lifecycle Shift:** A product is composed of `ProductVariant`s. *However, unlike the previous version, variants now have an independent lifecycle.* They are created with the product but updated/deleted via their own dedicated endpoints.
- **Key Fields:**
  - `slug`: The store-scoped URL identifier.
  - `isActive`: Boolean flag dictating public visibility, which now triggers explicit lifecycle hooks (`activation`/`deactivation`).
  - `targetedAudience` & `ageGroup`: Enums dictating the demographic scope.

---

### 2. Roles & Access Model
The API strictly partitions access between two domains:

- **Public Role (`PublicProductController`):**
  - **Allowed:** Read-only access to browse and view active products.
  - **Forbidden:** Accessing inactive/draft products or mutating any data.
  - **Enforcement:** The controller forces `filter.setIsActive(true)`. Direct endpoint fetches explicitly check `product.getIsActive()` and throw an exception if false.
- **Store Role (`StoreProductController`):**
  - **Allowed:** Full CRUD operations on products and individual variants belonging to their `storeId`.
  - **Forbidden:** Modifying or viewing products/variants of other stores.
  - **Enforcement:** `storeId` is bound via the URL path (`@PathVariable`), implicitly trusting the path and overriding payload values to prevent ID spoofing.

---

### 3. Business Rules

#### General Rules
- **Data Segregation:** Products and their slugs are strictly partitioned by `storeId`.
- **Mandatory Default Variant:** A product MUST have exactly one default variant (`isDefault = true`). Having zero or multiple defaults is forbidden.
- **Variant Attribute Requirement:** If a product contains more than one variant, *every* variant must have at least one defined `VariantAttribute` (to differentiate them).

#### Creation Rules
- **Atomic Creation:** When creating a product, you *must* supply the variants in the payload (`@NotNull(groups = OnCreate.class)`).
- **Dual Demographic Compatibility:** A product's `targetedAudience` and `ageGroup` must logically match **BOTH** its parent `Category` AND its parent `Brand` (unless the parent's audience/age is set to `ALL`).

#### Update Rules (Major Changes)
- **Separation of Concerns:** You can no longer update a product and its variants in the same request. The product update payload explicitly forbids variants (`@Null(groups = OnUpdate.class)`).
- **Variant Independence:** Variants must be updated or deleted individually via dedicated endpoints (`PUT .../variants` and `DELETE .../variants/{id}`).
- **State Machine Hooks:** Toggling the `isActive` flag from `false` to `true` triggers an `activation()` routine, and `true` to `false` triggers `deactivation()`.
- **Immutability:** Once created, a product cannot be moved to a different `mallId` or `storeId`.

#### Delete Rules
- **Ownership Verification:** A product or variant can only be deleted if the authenticated `storeId` matches the product's owner.
- **Hard Deletion:** Deleting a product triggers a hard cascade delete (`CascadeType.ALL`), permanently removing variants, media, and tags from the database.

#### Access Rules
- **Graceful External Degradation:** Pricing and Media data are fetched dynamically via Feign clients (`CampaignsClient`, `MediaManagerClient`). If the Campaign service is down, the system gracefully falls back to base prices without failing the request.

---

### 4. Validation Rules
**Product Level:**
- `id`: Must be null on creation, must be provided on update.
- `slug`: Must be 3-50 chars, strictly lowercase, alphanumeric with dashes, no whitespace, starting and ending with a letter.
- `variants`: **Required** on POST (Create). **Forbidden** on PUT (Update).

**Variant Level:**
- `basePrice`: Must be strictly a positive number (`@Positive`).
- `media`: Every variant must have associated media attached at creation.

---

### 5. Hidden / Implicit Rules
- **Store-Level Slug Scoping:** Slugs are not globally unique across the platform. Store A and Store B can both safely have a product slugged `samsung-s24`.
- **Dynamic Pricing Mastery:** The Catalog module does not own final pricing. The `hasDiscount`, `discountedPrice`, and `offerId` fields are transient and injected entirely at runtime by the `CampaignsClient`.
- **Tag Auto-Resolution:** When saving a product, the system automatically resolves provided tags. If a tag does not exist, it is implicitly created behind the scenes via `tagService.resolveTags()`.

---

### 6. Potential Issues / Risks

2. **Database Connection Pool Exhaustion:**
  - In `ServiceHelper`, the `injectMedium` and `injectDiscount` methods make synchronous network calls via Feign. Because `ProductServiceImpl.getById` runs inside a `@Transactional` block, the database connection remains open while waiting for the network. If the external services lag, your DB connection pool will exhaust quickly.
  - **Fix:** Fetch external data *outside* the `@Transactional` boundary whenever possible.
3. **Hard Deletes Instead of Soft Deletes:**
  - Calling `productRepository.delete(product)` physically wipes the record. In e-commerce, products tied to historical orders should be soft-deleted (e.g., `isDeleted = true`) to maintain referential integrity for reporting and past invoices.
4. **Hardcoded Security Context:**
  - The controllers are still passing a hardcoded `1L` for `mallId`. This is a critical security placeholder that must be tied to the JWT/Auth token before production.