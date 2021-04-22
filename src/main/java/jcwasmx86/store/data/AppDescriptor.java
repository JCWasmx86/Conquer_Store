package jcwasmx86.store.data;

import java.net.URL;

public record AppDescriptor(URL downloadBundle, String name, String description, String logo, String[] imageURLs) {

}
