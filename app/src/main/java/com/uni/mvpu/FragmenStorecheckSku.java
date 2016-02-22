package com.uni.mvpu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Entitys.orderControlParams;
import interfaces.IOrderTotal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmenStorecheckSku.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmenStorecheckSku#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmenStorecheckSku extends Fragment implements IOrderTotal {
    private View parentView;


//    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
//    public static FragmenStorecheckSku newInstance(String param1, String param2) {
//        FragmenStorecheckSku fragment = new FragmenStorecheckSku();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public FragmenStorecheckSku() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragmen_storecheck_sku, container, false);
        return parentView;

    }

    // TODO: Rename method, update argument and hook method into UI event


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public orderControlParams displayTotal() {
        return null;
    }

    @Override
    public Boolean allowCloseOrder() {
        return null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
