package com.example.quizquest;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.example.quizquest.Adapters.CategoryAdapter;

import static com.example.quizquest.DbQuery.g_catList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    private GridView catGrid;
//    private LinearLayout liveTest;
//    private TextView livetestTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        catGrid = view.findViewById(R.id.catGridview);
//        liveTest = view.findViewById(R.id.live_test);
//        livetestTime = view.findViewById(R.id.time);

        TextView title = getActivity().findViewById(R.id.title);
        title.setText("Categories");


        CategoryAdapter adapter = new CategoryAdapter(g_catList);
        catGrid.setAdapter(adapter);

//        if(DbQuery.liveTestID != null)
//        {
//            liveTest.setVisibility(View.VISIBLE);
//
//            SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
//            //sfd.format(new Date(DbQuery.testTime));
//            String dd = sfd.format(DbQuery.testTime.toDate());
//
//            livetestTime.setText("Start at " + dd);
//        }
//        else
//        {
//            liveTest.setVisibility(View.GONE);
//        }


        return view;
    }


}
