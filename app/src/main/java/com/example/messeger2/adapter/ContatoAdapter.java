package com.example.messeger2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.messeger2.R;
import com.example.messeger2.model.Contato;

import java.util.ArrayList;
import java.util.List;

public class ContatoAdapter extends ArrayAdapter {

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(@NonNull Context c, @NonNull ArrayList objects) {
        super(c,0, objects);
        this.contatos = objects;
        this.context = c;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(contatos != null){
            //inicializar obejto
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Montar View
            view = inflater.inflate(R.layout.lista_contato, parent,false);

            //recuperar elemento
           TextView nomeContato= view.findViewById(R.id.tv_nome);
           TextView emailContato= view.findViewById(R.id.tv_email);

          Contato contato = contatos.get(position);
          nomeContato.setText(contato.getNome());
          emailContato.setText(contato.getEmail());
        }
        return view;
    }
}
