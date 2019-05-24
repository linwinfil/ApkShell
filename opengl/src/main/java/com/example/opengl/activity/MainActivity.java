package com.example.opengl.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.opengl.R;
import com.example.opengl.gl.utils.GlUtils;
import com.example.opengl.utils.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
{

    ArrayList<Object[]> classSparseArray = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(this);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        GridItemDecoration gridItemDecoration = new GridItemDecoration();
        recyclerView.addItemDecoration(gridItemDecoration);


        classSparseArray.add(new Object[]{"gl_image", GlImageActivity.class});
        classSparseArray.add(new Object[]{"gl_fbo", GlFboActivity.class});
        classSparseArray.add(new Object[]{"gl_blend", GlBlendActivity.class});
        classSparseArray.add(new Object[]{"gl_oes", GlVideoActivity.class});
        classSparseArray.add(new Object[]{"gl_shape", GlShapeActivity.class});

        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        float glSupportVersion = GlUtils.getGlSupportVersion(this);
        System.out.println("glSupportVersion :" + glSupportVersion);
    }

    private class Adapter extends RecyclerView.Adapter
    {

        class Holder extends RecyclerView.ViewHolder
        {

            Holder(@NonNull View itemView)
            {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            Button button = new Button(parent.getContext());
            button.setBackgroundColor(Color.LTGRAY);
            button.setGravity(Gravity.CENTER);
            button.setTextSize(16);
            button.setTextColor(Color.BLACK);
            button.setAllCaps(false);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(Utils.sScreenW / 4, Utils.sScreenW / 4);
            button.setLayoutParams(params);
            return new Holder(button);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            Object[] objects = classSparseArray.get(position);
            ((Button) holder.itemView).setOnClickListener(onClickListener);
            ((Button) holder.itemView).setText(objects[0].toString());
            ((Button) holder.itemView).setTag(objects[1]);
        }

        @Override
        public int getItemCount()
        {
            return classSparseArray.size();
        }

        private View.OnClickListener onClickListener = v ->
        {
            Class tag = (Class) v.getTag();
            Intent intent = new Intent(MainActivity.this, tag);
            MainActivity.this.startActivity(intent);
        };
    }

    private class GridItemDecoration extends RecyclerView.ItemDecoration
    {

        int spanCount = 4;
        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount)
            {
                outRect.top = (int) (spacing * 1f/ spanCount);
            }
        }
    }
}
