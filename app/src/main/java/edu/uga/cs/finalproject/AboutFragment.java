package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} that displays information about the application or
 * project.
 * Use the {@link AboutFragment#newInstance(String, String)} factory method
 * to create an instance of this fragment with optional parameters.
 */
public class AboutFragment extends Fragment {

    /** Argument key for the first parameter. */
    private static final String ARG_PARAM1 = "param1";

    /** Argument key for the second parameter. */
    private static final String ARG_PARAM2 = "param2";

    /** First initialization parameter (optional). */
    private String mParam1;

    /** Second initialization parameter (optional). */
    private String mParam2;

    /**
     * Required empty public constructor.
     */
    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Optional first parameter.
     * @param param2 Optional second parameter.
     * @return A new instance of fragment AboutFragment.
     */
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to do initial creation of the fragment.
     * 
     * Retrieves and stores any initialization parameters supplied
     * via {@link #newInstance(String, String)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check arguments are null
        Bundle args = getArguments();
        if (args != null) {
            mParam1 = args.getString(ARG_PARAM1);
            mParam2 = args.getString(ARG_PARAM2);
        } // if statement
    }

    /**
     * Inflates and returns the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view that this fragment's UI should
     *                           attach to.
     * @param savedInstanceState If non-null, this fragment is being re-created from
     *                           a previous state.
     * @return The root View for this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}