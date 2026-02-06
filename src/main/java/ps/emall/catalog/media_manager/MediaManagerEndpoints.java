package ps.emall.catalog.media_manager;

public final class MediaManagerEndpoints {

    private MediaManagerEndpoints() {}

    public static final String UPLOAD = "/media/upload";
    public static final String DELETE = "/media/{id}";
    public static final String GET_BY_ID = "/media/{id}";
    public static final String IS_FILE_EXIST = "/media/{fileKey}/exists";
}

