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

    public static String generateUniqueFilename(String path){
        int parent = 0;
        int name = 1;
        int extension = 2;
        try{
            String[] parts = getPartsOfPath(path);

            int lastOpen = parts[name].lastIndexOf('(');
            int lastClose = parts[name].lastIndexOf(')');

            if(lastOpen != -1 && lastClose != -1){
                int num = 0;
                lastOpen += 1;
                while(lastOpen < lastClose){
                    if(!Character.isDigit(parts[name].charAt(lastOpen))){
                        parts[name] = parts[name] + "(1)";
                        path = parts[parent] + parts[name] + parts[extension];
                        return path;
                    }
                    num = num * 10 + Character.getNumericValue(parts[name].charAt(lastOpen));
                    lastOpen++;
                }
                String prefix = parts[name].substring(0, lastOpen-1);
                String suffix = parts[name].substring(lastClose);
                num = num + 1;
                parts[name] = prefix + num + suffix;
            }else{
                parts[name] = parts[name] + "(1)";
            }

            path = parts[parent] + parts[name] + parts[extension];

            return path;
        }catch (Exception ex){
            System.err.println(ex.getMessage());
            return "";
        }
    }

    private static String[] getPartsOfPath(String path){
        var oPath = Paths.get(path);
        String parent = oPath.getParent().toString();
        String baseName = oPath.getFileName().toString();
        String extension = "";

        int dotIndex = baseName.lastIndexOf('.');

        if (dotIndex != -1){
            extension = baseName.substring(dotIndex);
            baseName = baseName.substring(0, dotIndex);
        }

        return new String[] { parent, baseName, extension };
    }
}
