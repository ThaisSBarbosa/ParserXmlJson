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

            Pattern pValor = Pattern.compile("^[1-9]\\d*(\\.\\d+)?$");
            Matcher mValor = pValor.matcher(valor.substring(1, valor.indexOf("||")));
            if (mValor.find()) {
                valor = valor.replace("||", ",");     //é número
                valor = valor.replace("|", "");
            } else {
                valor = valor.replace("||", "\",");   //é texto
                valor = valor.replace("|", "\"");
            }

            inputXml = inputXml.replace(m.group(), valor);
        }

        inputXml = inputXml.replace(",\n\t,", "\n},");
        inputXml = inputXml.replace(",\n\t}", "\n}");
        inputXml = inputXml.replace(":|", ":{");
        inputXml = inputXml.replace(",\n||", "}");

        System.out.println(inputXml);

    }
}
