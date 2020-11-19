package ass07;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/19
 * @version1    1.0
 */

public class HTTPResponse {
    private StringBuilder sbuilder; // usata per costruire l'header
    private String header;          //

    /** ex. response
     * HTTP/1.1 200 OK
     * Date: Sun, 14 May 2000 19:57:13 GMT
     * Server: Apache/1.3.9 (Unix) (Red Hat/Linux)
     * Last-Modified: Tue, 21 Sep 1999 14:46:36 GMT
     * ETag: "f2fc-799-37e79a4c"
     * Accept-Ranges: bytes
     * Content-Length: 1945
     * Connection: close
     * Content-Type: text/html
     */
    public HTTPResponse() {
        sbuilder = new StringBuilder();
        this.sbuilder.append("HTTP/1.1 ");
    }

    public void insertEmptyLine() {
        this.sbuilder.append("\r\n");
    }

    /**
     * setta lo Status Code da mandare nella Response
     * @param code
     */
    public void setStatusCode(int code) {
        StringBuilder sbTMP = new StringBuilder();
        switch(code) {
            case 200:
                sbTMP.append("200 OK\r\n");
                break;
            case 404:
                sbTMP.append("404 Not Found\r\n");
                break;
            default:
                break;
        }

        this.sbuilder.append(sbTMP.toString());
        this.sbuilder.append("Server: Rancoroso\r\n");
    }
    /**
     *
     * @param extension in base all'estensione setta il Content-Type
     */
    public void setContentType(String extension) {
        String tmp = null;

        switch(extension) {
            case ".jpg":
            case ".jpeg":
                tmp = "image/jpg\r\n";
                break;
            case ".html":
                tmp = "text/html\r\n";
                break;
            case ".txt":
                tmp = "text/plain\r\n";
                break;
            case ".gif":
                tmp = "image/gif\r\n";
                break;
            default: ;
        }

        this.sbuilder.append("Content-Type: ");
        this.sbuilder.append(tmp);
    }

    @Override
    public String toString(){
        return this.sbuilder.toString();
    }
}
