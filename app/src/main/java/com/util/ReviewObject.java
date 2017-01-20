package com.util;

/**
 * Created by adventis on 3/16/15.
 */
public class ReviewObject {
        private final String separator = "_SEPARATOR_";
        public String name;
        public String comment;
        public int stars;
        public String id;


        public ReviewObject() {
            this.name = "";
            this.comment = "";
            this.stars = 0;
            this.id = "";
        }

        public ReviewObject(String name, String comment, int stars, String id) {
            this.name = name;
            this.comment = comment;
            this.stars = stars;
            this.id = id;
        }

        public String convertToString() {
            String object = name+separator+comment+separator+stars+separator+id;
            return object;
        }

        public void convertFromString(String serizableString) {
            String[] parsedArray = serizableString.split(separator);
            if(parsedArray.length == 4) {
                this.name = parsedArray[0];
                this.comment = parsedArray[1];
                this.stars = Integer.parseInt(parsedArray[2]);
                this.id = parsedArray[3];
            }

        }
}
