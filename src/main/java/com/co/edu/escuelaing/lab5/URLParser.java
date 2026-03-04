package com.co.edu.escuelaing.lab5;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class URLParser {
    public static void main(String[] args) throws URISyntaxException, MalformedURLException {
        URL myURL = new URI("https://ldbn.is.escuelaing.edu.co:7865/arep/respuestasexamen.txt?val=3&t=4#examenfinal").toURL();
        System.out.println("Protocol:" + myURL.getProtocol());
        System.out.println("Host:" + myURL.getHost());
        System.out.println("Port:" + myURL.getPort());
        System.out.println("Authority:" + myURL.getAuthority());
        System.out.println("Path:" + myURL.getPath());
        System.out.println("File:" + myURL.getFile());
        System.out.println("Query:" + myURL.getQuery());
        
    }

}
