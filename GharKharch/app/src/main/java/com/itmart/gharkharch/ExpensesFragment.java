package com.itmart.gharkharch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import Adapters.ExpensesAdapter;

public class ExpensesFragment extends Fragment {

    private static String paramExpenses = "expenses_list";
    private ExpensesAdapter adapter;
    private ArrayList<Integer> expensesList;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    public static ExpensesFragment newInstance(ArrayList<Integer> param1) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(String.valueOf(paramExpenses), param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expensesList = getArguments().getIntegerArrayList(paramExpenses);
        }
        adapter = new ExpensesAdapter(requireContext(), expensesList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}