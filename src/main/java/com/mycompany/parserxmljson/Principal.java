package com.mycompany.parserxmljson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Principal {

    public static void main(String[] args) throws Exception {

        String inputXml = ExemploXML.inputXml;
        int posInicioTag = 0;

        while (posInicioTag > -1) {
            posInicioTag = inputXml.indexOf("<");

            if (posInicioTag > -1) {
                int posFinalTag = inputXml.indexOf(">", posInicioTag);
                String tag = inputXml.substring(posInicioTag + 1, posFinalTag);

                inputXml = inputXml.replaceAll("<" + tag + ">", "\"" + tag + "\":|");
                inputXml = inputXml.replaceAll("</" + tag + ">", "||");
            } else {
                break;
            }
        }

        Pattern p = Pattern.compile("[|][A-Za-z0-9. ][^:]*[||]");
        Matcher m = p.matcher(inputXml);
        while (m.find()) {
            var valor = m.group().replace("||\n||", "}");
            valor = valor.replace("||", ",");
            valor = valor.replace("|", "");
            /*
             * valor = valor.replace("||", "\",");
             * valor = valor.replace("|", "\"");
             */
            inputXml = inputXml.replace(m.group(), valor);
        }

        inputXml = inputXml.replace(",\n\t,", "\n},");
        inputXml = inputXml.replace(",\n\t}", "\n}");
        inputXml = inputXml.replace(":|", ":{");
        inputXml = inputXml.replace(",\n||", "}");

        System.out.println(inputXml);

    }
}
