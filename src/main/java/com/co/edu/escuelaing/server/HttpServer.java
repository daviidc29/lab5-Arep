// HttpServer.java
package com.co.edu.escuelaing.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    static Map<String, WebMethod> endPoints = new HashMap<>();
    static String staticFilesPath = "/webroot/public";

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8080.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;

        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            OutputStream dataOut = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;
            boolean isFirstLine = true;
            String reqpath = "";
            String requery = "";
            String method = "";
            String protocolversion = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);

                if (isFirstLine) {
                    String[] flTokens = inputLine.split(" ");
                    method = flTokens[0];
                    String struripath = flTokens[1];
                    protocolversion = flTokens[2];

                    URI uripath = new URI(struripath);
                    reqpath = uripath.getPath();
                    requery = uripath.getQuery();

                    System.out.println("Path: " + reqpath);
                    isFirstLine = false;
                }

                if (!in.ready()) {
                    break;
                }
            }

            HttpRequest req = new HttpRequest(method, reqpath, protocolversion, requery);
            HttpResponse res = new HttpResponse();

            WebMethod currentWm = endPoints.get(reqpath);
            if (currentWm != null) {
                String body = currentWm.execute(req, res);
                if (body == null) {
                    body = "";
                }

                outputLine = buildTextResponse(
                        res.getStatusCode(),
                        res.getStatusMessage(),
                        res.getContentType(),
                        body
                );

                out.print(outputLine);
                out.flush();

            } else {
                byte[] staticResponse = buildStaticFileResponse(reqpath);

                if (staticResponse != null) {
                    dataOut.write(staticResponse);
                    dataOut.flush();
                } else {
                    outputLine = buildTextResponse(
                            404,
                            "Not Found",
                            "text/html; charset=UTF-8",
                            "<!DOCTYPE html>"
                                    + "<html>"
                                    + "<head>"
                                    + "<meta charset=\"UTF-8\">"
                                    + "<title>My Web Site</title>"
                                    + "</head>"
                                    + "<body>"
                                    + "<h1>My Web Site </h1>"
                                    + "<p>Resource not found: " + reqpath + "</p>"
                                    + "</body>"
                                    + "</html>"
                    );

                    out.print(outputLine);
                    out.flush();
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }

    public static void staticfiles(String path) {
        if (path == null || path.trim().isEmpty()) {
            staticFilesPath = "/webroot/public";
        } else {
            staticFilesPath = path.startsWith("/") ? path : "/" + path;
        }
    }

    private static String buildTextResponse(int statusCode, String statusMessage, String contentType, String body) {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;
    }

    private static byte[] buildStaticFileResponse(String reqpath) throws IOException {
        String resourcePath = reqpath.equals("/") ? "/index.html" : reqpath;
        String fullPath = staticFilesPath + resourcePath;

        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }

        InputStream resourceStream = HttpServer.class.getClassLoader().getResourceAsStream(fullPath);

        if (resourceStream == null) {
            return null;
        }

        byte[] fileBytes = readAllBytes(resourceStream);
        String contentType = getContentType(resourcePath);

        String headers = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + fileBytes.length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        response.write(headers.getBytes(StandardCharsets.UTF_8));
        response.write(fileBytes);

        return response.toByteArray();
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        inputStream.close();
        return buffer.toByteArray();
    }

    private static String getContentType(String path) {
        String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(".html") || lowerPath.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        }
        if (lowerPath.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (lowerPath.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (lowerPath.endsWith(".png")) {
            return "image/png";
        }
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerPath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lowerPath.endsWith(".ico")) {
            return "image/x-icon";
        }

        return "text/plain; charset=UTF-8";
    }
}