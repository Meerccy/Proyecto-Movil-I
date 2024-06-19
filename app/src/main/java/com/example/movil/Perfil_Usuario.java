package com.example.movil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import com.example.movil.InicioFragment;

//import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

public class Perfil_Usuario extends AppCompatActivity {

    private InicioFragment myFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        myFragment = new InicioFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragments, myFragment)
                .commit();

    }

}