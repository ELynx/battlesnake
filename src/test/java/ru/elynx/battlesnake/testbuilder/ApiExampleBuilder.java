package ru.elynx.battlesnake.testbuilder;

public class ApiExampleBuilder {
    private ApiExampleBuilder() {
    }

    public static String gameState() {
        return "{\n" + "  \"game\": {\n" + "    \"id\": \"game-00fe20da-94ad-11ea-bb37\",\n" + "    \"ruleset\": {\n"
                + "      \"name\": \"standard\",\n" + "      \"version\": \"v.1.2.3\"\n" + "    },\n"
                + "    \"timeout\": 500\n" + "  },\n" + "  \"turn\": 14,\n" + "  \"board\": {\n"
                + "    \"height\": 11,\n" + "    \"width\": 11,\n" + "    \"food\": [\n"
                + "      {\"x\": 5, \"y\": 5}, \n" + "      {\"x\": 9, \"y\": 0}, \n" + "      {\"x\": 2, \"y\": 6}\n"
                + "    ],\n" + "    \"hazards\": [\n" + "      {\"x\": 0, \"y\": 0}\n" + "    ],\n"
                + "    \"snakes\": [\n" + "      {\n" + "        \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n"
                + "        \"name\": \"My Snake\",\n" + "        \"health\": 54,\n" + "        \"body\": [\n"
                + "          {\"x\": 0, \"y\": 0}, \n" + "          {\"x\": 1, \"y\": 0}, \n"
                + "          {\"x\": 2, \"y\": 0}\n" + "        ],\n" + "        \"latency\": \"111\",\n"
                + "        \"head\": {\"x\": 0, \"y\": 0},\n" + "        \"length\": 3,\n"
                + "        \"shout\": \"why are we shouting??\",\n" + "        \"squad\": \"\"\n" + "      }, \n"
                + "      {\n" + "        \"id\": \"snake-b67f4906-94ae-11ea-bb37\",\n"
                + "        \"name\": \"Another Snake\",\n" + "        \"health\": 16,\n" + "        \"body\": [\n"
                + "          {\"x\": 5, \"y\": 4}, \n" + "          {\"x\": 5, \"y\": 3}, \n"
                + "          {\"x\": 6, \"y\": 3},\n" + "          {\"x\": 6, \"y\": 2}\n" + "        ],\n"
                + "        \"latency\": \"222\",\n" + "        \"head\": {\"x\": 5, \"y\": 4},\n"
                + "        \"length\": 4,\n" + "        \"shout\": \"I'm not really sure...\",\n"
                + "        \"squad\": \"THIS WAS NOT IN EXAMPLE\"\n" + "      }\n" + "    ]\n" + "  },\n"
                + "  \"you\": {\n" + "    \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n"
                + "    \"name\": \"My Snake\",\n" + "    \"health\": 54,\n" + "    \"body\": [\n"
                + "      {\"x\": 0, \"y\": 0}, \n" + "      {\"x\": 1, \"y\": 0}, \n" + "      {\"x\": 2, \"y\": 0}\n"
                + "    ],\n" + "    \"head\": {\"x\": 0, \"y\": 0},\n" + "    \"length\": 3,\n"
                + "    \"shout\": \"why are we shouting??\"\n" + "  }\n" + "}";
    }

    public static String standardRulesetName() {
        return "standard";
    }

    public static String royaleRulesetName() {
        return "royale";
    }
}
