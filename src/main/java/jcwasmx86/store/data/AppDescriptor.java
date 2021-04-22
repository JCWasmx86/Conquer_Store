package jcwasmx86.store.data;

import java.net.URL;

public record AppDescriptor(URL baseURL, String name, String description, String logo, String[] imageURLs) {

}
