package com.example.messeger2.adapter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {

    private Context context;
    private SharedPreferences prerences;
    private String NOME_ARQUIVO = "Mensseger preferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;
    private String CHAVE_IDENTIFICADOR="identificadorUsuarioLogin";
    private String CHAVE_NOME="nomeUsuarioLogin";


    public Preferencias (Context contextParamentro){
        context = contextParamentro;
        prerences = context.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor= prerences.edit();
    }
    public void salvarDados(String identificadorUsuario, String nomeUsuario){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();

    }
    public  String getIdentificador(){
        return prerences.getString(CHAVE_IDENTIFICADOR, null);

    }
    public  String getNome(){
        return prerences.getString(CHAVE_NOME, null);
    }

}
