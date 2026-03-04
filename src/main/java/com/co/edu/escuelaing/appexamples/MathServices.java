package com.co.edu.escuelaing.appexamples;

import java.io.IOException;
import java.net.URISyntaxException;

import com.co.edu.escuelaing.server.HttpServer;
import static com.co.edu.escuelaing.server.HttpServer.get;

public class MathServices {

    public static void main(String[] args) throws IOException, URISyntaxException {
        get("/pi", (req, res) -> "Pi: " + Math.PI);
        get("/hello", (req, res) -> "Hello " + req.getValue("name"));
        get("/frommethod", (req, res) -> getEuler());
        HttpServer.main(args);

    }

    private static String getEuler() {
        return "e: " + Math.E;
    }
}
