package com.itmart.gharkharch.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.ExpensesAdapter;

public class ExpensesFragment extends Fragment {

    public static String paramExpenses = "expenses_list";
    private ExpensesAdapter adapter;
    private List<Integer> expensesList;
    private RecyclerView expensesRCV;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    public static ExpensesFragment newInstance(List<Integer> param1) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(String.valueOf(paramExpenses), (ArrayList<Integer>) param1);
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
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        //recycler view
        expensesRCV = view.findViewById(R.id.expensesRCV);
        expensesRCV.setLayoutManager(new LinearLayoutManager(requireContext()));
        expensesRCV.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
}