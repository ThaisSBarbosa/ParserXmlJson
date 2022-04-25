package com.mycompany.parserxmljson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Principal {

    public static void main(String[] args) throws Exception {

        String inputXml = ExemploXML.inputXml;
        int posicaoInicioChave = 0;

        //enquanto houver uma nova chave, faz as substituições
        while (posicaoInicioChave > -1) {
            posicaoInicioChave = inputXml.indexOf("<");

            if (posicaoInicioChave > -1) {
                int posicaoFinalChave = inputXml.indexOf(">", posicaoInicioChave);
                String chave = inputXml.substring(posicaoInicioChave + 1, posicaoFinalChave);

                inputXml = marcaInicioEFinalValorDaChave(inputXml, chave);     
            } else {
                break;      //se não houver chave, sai do laço
            }
        }

        // regex para pegar valores entre | e ||
        Matcher m = Pattern.compile("[|][A-Za-z0-9. ][^:]*[||]").matcher(inputXml);
        while (m.find()) {            
            var valor = m.group().replace("||\n||", "}");
            inputXml = ajustaValoresTextoOuNumericos(valor, inputXml, m);
        }

        inputXml = inputXml.replace(",\n\t,", "\n},");
        inputXml = inputXml.replace(",\n\t}", "\n}");
        inputXml = inputXml.replace(":|", ":{");
        inputXml = inputXml.replace(",\n||", "}");

        System.out.println(inputXml);
    }

    /// Marca início do valor com | e final com ||.
    private static String marcaInicioEFinalValorDaChave(String inputXml, String chave) {
        inputXml = inputXml.replaceAll("<" + chave + ">", "\"" + chave + "\":|");     //substitui por ex. <tag> por chave:|
        inputXml = inputXml.replaceAll("</" + chave + ">", "||");                     //substitui por ex. </tag> por ||
        return inputXml;
    }

    private static String ajustaValoresTextoOuNumericos(String valor, String inputXml, Matcher m) {
        
        // regex para pegar números inteiros e decimais
        Matcher mValor = Pattern.compile("^[1-9]\\d*(\\.\\d+)?$").matcher(valor.substring(1, valor.indexOf("||")));
        
        if (mValor.find()) {
            valor = valor.replace("||", ",");     //é número
            valor = valor.replace("|", "");
        } else {
            valor = valor.replace("||", "\",");   //é texto
            valor = valor.replace("|", "\"");
        }
        
        inputXml = inputXml.replace(m.group(), valor);
        return inputXml;
    }
}
