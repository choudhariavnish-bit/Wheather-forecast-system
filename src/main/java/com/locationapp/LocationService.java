package com.locationapp;

public class LocationService {

    public String getLocationsJSON() {
        return "{\n" +
                "  \"India\": {\n" +
                "    \"Maharashtra\": [\"Chh. Sambhajinagar\", \"Mumbai\", \"Pune\", \"Nagpur\", \"Nashik\", \"Thane\"],\n" +
                "    \"Delhi\": [\"New Delhi\", \"North Delhi\", \"South Delhi\", \"East Delhi\"],\n" +
                "    \"Karnataka\": [\"Bengaluru\", \"Mysuru\", \"Mangaluru\", \"Hubballi\"],\n" +
                "    \"Tamil Nadu\": [\"Chennai\", \"Coimbatore\", \"Madurai\", \"Salem\"],\n" +
                "    \"Uttar Pradesh\": [\"Noida\", \"Lucknow\", \"Kanpur\", \"Agra\"]\n" +
                "  },\n" +
                "  \"United States\": {\n" +
                "    \"California\": [\"Los Angeles\", \"San Francisco\", \"San Diego\", \"San Jose\", \"Sacramento\"],\n" +
                "    \"New York\": [\"New York City\", \"Buffalo\", \"Albany\", \"Rochester\"],\n" +
                "    \"Texas\": [\"Houston\", \"Austin\", \"Dallas\", \"San Antonio\"],\n" +
                "    \"Florida\": [\"Miami\", \"Orlando\", \"Tampa\", \"Jacksonville\"]\n" +
                "  },\n" +
                "  \"United Kingdom\": {\n" +
                "    \"England\": [\"London\", \"Manchester\", \"Birmingham\", \"Liverpool\"],\n" +
                "    \"Scotland\": [\"Edinburgh\", \"Glasgow\", \"Aberdeen\"]\n" +
                "  },\n" +
                "  \"Canada\": {\n" +
                "    \"Ontario\": [\"Toronto\", \"Ottawa\", \"Hamilton\"],\n" +
                "    \"Quebec\": [\"Montreal\", \"Quebec City\"],\n" +
                "    \"British Columbia\": [\"Vancouver\", \"Victoria\"]\n" +
                "  },\n" +
                "  \"Japan\": {\n" +
                "    \"Tokyo\": [\"Tokyo\", \"Shinjuku\", \"Shibuya\"],\n" +
                "    \"Osaka\": [\"Osaka\", \"Sakai\"],\n" +
                "    \"Kyoto\": [\"Kyoto\", \"Uji\"]\n" +
                "  },\n" +
                "  \"Germany\": {\n" +
                "    \"Bavaria\": [\"Munich\", \"Nuremberg\"],\n" +
                "    \"Berlin\": [\"Berlin\", \"Mitte\"]\n" +
                "  },\n" +
                "  \"France\": {\n" +
                "    \"Île-de-France\": [\"Paris\", \"Versailles\"],\n" +
                "    \"Provence-Alpes-Côte d'Azur\": [\"Nice\", \"Marseille\"]\n" +
                "  }\n" +
                "}";
    }
}
