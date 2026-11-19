package com.example.alquilervehiculos.adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class AdaptadorGaleriaImagenes extends RecyclerView.Adapter<AdaptadorGaleriaImagenes.ImagenViewHolder> {

    private final Context context;
    private final ArrayList<String> listaRutasImagenes;

    public AdaptadorGaleriaImagenes(Context context, ArrayList<String> listaRutasImagenes) {
        this.context = context;
        this.listaRutasImagenes = listaRutasImagenes != null && !listaRutasImagenes.isEmpty()
                ? listaRutasImagenes : crearListaPorDefecto();
    }

    private ArrayList<String> crearListaPorDefecto() {
        ArrayList<String> defecto = new ArrayList<>();
        defecto.add("ic_car_placeholder");
        return defecto;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_galeria_imagen, parent, false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        String ruta = listaRutasImagenes.get(position);

        if (ruta == null || ruta.isEmpty()) {
            holder.imagenGaleria.setImageResource(R.drawable.ic_car_placeholder);
            return;
        }

        if (ruta.startsWith("/")) {
            // Archivo local en almacenamiento interno
            File file = new File(ruta);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    holder.imagenGaleria.setImageBitmap(bitmap);
                    return;
                }
            }
        } else if (ruta.startsWith("content://")) {
            // Content Uri
            try {
                Uri uri = Uri.parse(ruta);
                InputStream is = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (is != null) is.close();
                if (bitmap != null) {
                    holder.imagenGaleria.setImageBitmap(bitmap);
                    return;
                }
            } catch (Exception ignored) {}
        }

        // Búsqueda dinámica de recursos en res/drawable/ (hilux4x4, hyundayaccent2022, kiasportage, nissannp300)
        int resId = context.getResources().getIdentifier(ruta, "drawable", context.getPackageName());
        if (resId != 0) {
            holder.imagenGaleria.setImageResource(resId);
        } else {
            holder.imagenGaleria.setImageResource(R.drawable.ic_car_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return listaRutasImagenes.size();
    }

    public static class ImagenViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenGaleria;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenGaleria = itemView.findViewById(R.id.imagenGaleria);
        }
    }
}
