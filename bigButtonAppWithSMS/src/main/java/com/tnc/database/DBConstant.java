package com.tnc.database;

/**
 * Created by a3logics on 16/11/17.
 */

public class DBConstant {
    // Table Name
    static String TILE_TABLE = "Tiles";

    //Column Name
    public static String TILE_COLUMN_IMAGE_LOCK = "imageLock";

    //Column Type
    static String VARCHAR_TYPE  = "VARCHAR";
    static String BOOLEAN_TYPE =  "BOOL";


    //Query String
    static final String SQL_ALTER_TILE_TABLE = "ALTER TABLE " + TILE_TABLE +
            " ADD COLUMN " + TILE_COLUMN_IMAGE_LOCK +" "+ BOOLEAN_TYPE;

}
