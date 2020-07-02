package com.example.messeger2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.messeger2.R;
import com.example.messeger2.adapter.MensagemAdapter;
import com.example.messeger2.adapter.Preferencias;
import com.example.messeger2.config.ConfiguracaoFirebase;
import com.example.messeger2.helper.Base64Custom;
import com.example.messeger2.model.Conversa;
import com.example.messeger2.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton imageButton;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mesagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    //Dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    // Remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = findViewById(R.id.tb_conversa);
        editMensagem =findViewById(R.id.edt_mensagem);
        imageButton = findViewById(R.id.bt_envia);
        listView = findViewById(R.id.lv_conversas);

        //dados do usuário logado
        Preferencias preferencias  = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra= getIntent().getExtras();
        if(extra!= null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");

            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);

        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_seta);
        setSupportActionBar(toolbar);

        //Montar listview e adapter
        mesagens = new ArrayList<>();
        /*adapter = new ArrayAdapter(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mesagens
        );*/
        adapter = new MensagemAdapter(ConversaActivity.this, mesagens);
        listView.setAdapter(adapter);

        // Recuperar Mensagens
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("mensagem")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //Cri listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Limpar mensagens
                mesagens.clear();
               for(DataSnapshot dados: dataSnapshot.getChildren()){
                  Mensagem mensagem = dados.getValue(Mensagem.class);
                  mesagens.add(mensagem);
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firebase.addValueEventListener(valueEventListenerMensagem);

        //Enviar mensagem
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();
                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Escreve Carai", Toast.LENGTH_LONG).show();
                }else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //Salvar mensagem remetente
                    Boolean  retornoMR=salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if (!retornoMR){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar mensagem",
                                Toast.LENGTH_LONG).show();

                    }else {
                        //Salvamos mensagem para o destinatario
                        Boolean retornoMD= salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if(!retornoMD){
                            Toast.makeText(ConversaActivity.this, "Problema ao enviar mensagem ",Toast.LENGTH_LONG).show();
                        }
                    }
                    // Salvar conversa remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoCR= salvarConversa(idUsuarioRemetente,idUsuarioDestinatario, conversa);
                    if (!retornoCR){
                        Toast.makeText(ConversaActivity.this, "Problema em salvar conversa ",Toast.LENGTH_LONG).show();

                    }else{
                        // Salvar conversa destinatario
                        conversa = new Conversa();
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoCD = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa);
                        if (!retornoCD){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar conversa destinatario",
                                    Toast.LENGTH_LONG).show();
                        }
                    }



                    editMensagem.setText("");
                    /*
                    mensagens
                        +wilias@gmail
                            +willias@hotma
                                +mensagem
                            +wilias.silva@ufms
                                +mensagem
                     */

                }
            }
        });
    }
    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("Conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagem");

            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}