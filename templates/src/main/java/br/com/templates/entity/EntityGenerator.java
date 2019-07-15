package br.com.templates.entity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class EntityGenerator {

    private static final List<String> supportedTypes = asList("String", "Int", "Date");

    public String run(String nameClass, String argumentValue) {
        if (argumentValue.contains("references")) {
            return "" +
                    "@Data\n" +
                    "class User {\n" +
                    "    private String name;\n" +
                    "    private Integer age;\n" +
                    "    private List<Foo> foo;\n" +
                    "}\n";
        }

        String generatedClass = this.generateClass(nameClass);
        String[] attributes = argumentValue.split(" ");

        StringBuilder attributeToReplace = new StringBuilder();
        for (int i = 0; i < attributes.length; i++) {
            String attribute = generateAttribute(attributes[i]);
            attributeToReplace.append("\t".concat(attribute.concat("\n")));
        }
        generatedClass = generatedClass.replace("${attributes}", attributeToReplace.toString());

        return generatedClass;
    }

    public String generateAttribute(String attribute) {
        if (attribute.contains("references")) {
            String[] splitAttribute = attribute.split(":");
            String attributesReferences = getAttributesOfReferences(attribute);
            if (!attributesReferences.contains("relation")) throw new EntityValidator("should contain relation");
            String relation = getRelation(attributesReferences);

            if (relation.equals("belongsTo")) {
                return "private ".concat(splitAttribute[0]).concat(" ".concat(splitAttribute[0].toLowerCase()).concat(";"));
            }

            return "private List<".concat(splitAttribute[0]).concat(">").concat(" ".concat(splitAttribute[0].toLowerCase()).concat(";"));
        }

        if (!attribute.contains(":")) throw new EntityValidator("attribute should contains ':' ");
        String[] splitAttribute = attribute.split(":");
        if (!supportedTypes.contains(splitAttribute[1])) throw new EntityValidator(splitAttribute[1].concat(" not supported."));

        if (attribute.contains("Int")) return "private Integer ".concat(splitAttribute[0]).concat(";");
        if (attribute.contains("Date")) return "private Date ".concat(splitAttribute[0]).concat(";");
        if (attribute.contains("String")) return "private String ".concat(splitAttribute[0]).concat(";");
        return null;
    }

    public String generateClass(String nameClass) {
        return  "" +
                "@Data\n" +
                "public class ".concat(nameClass).concat(" {\n")+
                "${attributes}" +
                "}";
    }

    public Boolean metodoReturnTrue() {
        return true;
    }

    public String getAttributesOfReferences(String attributes) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(attributes);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public String getRelation(String attributes) {
        String[] splitAttributes = attributes.split(" ");
        String[] splitRelation = splitAttributes[0].split(":");
        return splitRelation[1];
    }

}
