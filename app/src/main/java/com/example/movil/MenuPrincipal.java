package com.example.movil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    Button AgregarNotas, ListarNotas, Importantes,Perfil,AcercaDe,CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView UidPrincipal, NombresPrincipal, CorreoPrincipal;
    Button EstadoCuentaPrincipal;
    ProgressBar progressBarDatos;
    ProgressDialog progressDialog;
    LinearLayoutCompat Linear_Nombres , Linear_Correo ,Linear_Verificacion;

    DatabaseReference Usuarios;

    Dialog dialog_cuenta_verificada, dialog_informacion, dialog_integrantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agenda Online");

        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        EstadoCuentaPrincipal = findViewById(R.id.EstadoCuentaPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        dialog_cuenta_verificada = new Dialog(this);
        dialog_informacion = new Dialog(this);
        dialog_integrantes = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor ...");
        progressDialog.setCanceledOnTouchOutside(false);

        Linear_Nombres = findViewById(R.id.Linear_Nombres);
        Linear_Correo = findViewById(R.id.Linear_Correo);
        Linear_Verificacion = findViewById(R.id.Linear_Verficacion);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");


        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Importantes = findViewById(R.id.Importantes);
        Perfil = findViewById(R.id.Perfil);
        AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        EstadoCuentaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isEmailVerified()){
                    //Si la cuenta está verificada
                    //Toast.makeText(MenuPrincipal.this, "Cuenta ya verificada", Toast.LENGTH_SHORT).show();
                    AnimacionCuentaVerificada();
                }else {
                    //Si la cuenta no está verificada
                    VerificarCuentaCorreo();
                }
            }
        });

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Obtenemos la información de los TextView*/
                String uid_usuario = UidPrincipal.getText().toString();
                String correo_usuario = CorreoPrincipal.getText().toString();

                /*Pasamos datos a la siguiente actividad*/
                Intent intent = new Intent(MenuPrincipal.this, Agregar_Notas.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);

            }
        });

        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });

        Importantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });

        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
                Toast.makeText(MenuPrincipal.this, "Integrantes", Toast.LENGTH_SHORT).show();
                Integrantes();
            }
        });

        AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
                Informacion();
            }
        });

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalirAplicacion();
            }
        });
    }

    private void VerificarCuentaCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("verificar cuenta")
                .setMessage("¿Estás seguro(a) de enviar instrucciones de verificación a su correo electrónico? "
                        +user.getEmail())
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EnviarCorreoAVerificacion();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuPrincipal.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void EnviarCorreoAVerificacion() {
        progressDialog.setMessage("Enviando instrucciones de verificación a su correo electrónico "+ user.getEmail());
        progressDialog.show();

        user.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Envío fue exitoso
                        progressDialog.dismiss();
                        Toast.makeText(MenuPrincipal.this, "Instrucciones enviadas, revise su bandeja " +user.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Falló el envío
                        Toast.makeText(MenuPrincipal.this, "Falló debido a: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void VerificarEstadoDeCuenta(){
        String Verificado = "Verificado";
        String No_Verificado = "No Verificado";
        if (user.isEmailVerified()){
            EstadoCuentaPrincipal.setText(Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(41, 128, 185));
        }else {
            EstadoCuentaPrincipal.setText(No_Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(231, 76, 60));
        }
    }

    private void AnimacionCuentaVerificada(){
        Button EntendidoVerificado;

        dialog_cuenta_verificada.setContentView(R.layout.dialogo_cuenta_verificada);

        EntendidoVerificado = dialog_cuenta_verificada.findViewById(R.id.EntendidoVerificado);

        EntendidoVerificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_cuenta_verificada.dismiss();
            }
        });

        dialog_cuenta_verificada.show();
        dialog_cuenta_verificada.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    private void ComprobarInicioSesion(){
        if (user!=null){
            //El usuario ha iniciado sesión
            CargaDeDatos();
        }else {
            //Lo dirigirá al MainActivity
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }
    }

    private void CargaDeDatos(){

        VerificarEstadoDeCuenta();

        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Si el usuario existe
                if (snapshot.exists()){
                    //El progressbar se oculta
                    progressBarDatos.setVisibility(View.GONE);
                    //Los TextView se muestran
                    //UidPrincipal.setVisibility(View.VISIBLE);
                    //NombresPrincipal.setVisibility(View.VISIBLE);
                    //CorreoPrincipal.setVisibility(View.VISIBLE);
                    Linear_Nombres.setVisibility(View.VISIBLE);
                    Linear_Correo.setVisibility(View.VISIBLE);
                    Linear_Verificacion.setVisibility(View.VISIBLE);

                    //Obtener los datos
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();

                    //Setear los datos en los respectivos TextView
                    UidPrincipal.setText(uid);
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                    //Habilitar los botones del menú
                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    Importantes.setEnabled(true);
                    Perfil.setEnabled(true);
                    AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void Informacion(){
        Button EntendidoInfo;

        dialog_informacion.setContentView(R.layout.cuadro_dialogo_informacion);

        EntendidoInfo = dialog_informacion.findViewById(R.id.EntendidoInfo);

        EntendidoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_informacion.dismiss();
            }
        });

        dialog_informacion.show();
        dialog_informacion.setCanceledOnTouchOutside(false);
    }
    private void Integrantes(){
        Button EntendidoInfo1;

        dialog_integrantes.setContentView(R.layout.fragment_inicio);

        EntendidoInfo1 = dialog_integrantes.findViewById(R.id.EntendidoInfo1);

        EntendidoInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_integrantes.dismiss();
            }
        });

        dialog_integrantes.show();
        dialog_integrantes.setCanceledOnTouchOutside(false);
    }


}