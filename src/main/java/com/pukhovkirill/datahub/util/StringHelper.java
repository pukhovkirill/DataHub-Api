package com.pukhovkirill.datahub.util;

import java.nio.file.Paths;

public class StringHelper {

    public static String extractName(String path){
        try{
            var oPath = Paths.get(path);
            return oPath.getFileName().toString();
        }catch (Exception ex){
            System.err.println(ex.getMessage());
            return "";
        }
    }
}
