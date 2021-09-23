package com.example.bootcampWeek4.ui.homeScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bootcampWeek4.base.BaseCallBack
import com.example.bootcampWeek4.databinding.FragmentHomeBinding
import com.example.bootcampWeek4.model.Task
import com.example.bootcampWeek4.response.TaskResponse
import com.example.bootcampWeek4.service.ServiceConnector
import com.example.bootcampWeek4.utils.gone
import com.example.bootcampWeek4.utils.toast
import com.example.bootcampWeek4.utils.visible

class HomeFragment : Fragment(),ITaskOnClickDelete,ITaskOnClickComplete{
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var homeAdapter: HomeAdapter = HomeAdapter()
    private lateinit var taskList: ArrayList<Task>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        taskList = arrayListOf()
        binding.taskRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        homeAdapter.addListener(this,this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getAllTask()
    }

    private fun getAllTask() {
        ServiceConnector.restInterface.getAllTask().enqueue(object : BaseCallBack<TaskResponse>(){
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(data: TaskResponse) {
                super.onSuccess(data)
                homeAdapter.setData(data.task)
                taskList = data.task
                binding.taskRecyclerView.adapter = homeAdapter
                binding.fabHomeFragment.visible()
                binding.taskRecyclerView.visible()
                binding.progressCircularHomeFragment.gone()
            }

            override fun onFailure() {
                super.onFailure()
                toast("GetAllTask is not running")
            }
        })
    }
    private fun deleteTask(position: Int){
        ServiceConnector.restInterface.deleteTaskById(taskList[position]._id).enqueue(object : BaseCallBack<Task>(){
            override fun onSuccess(data: Task) {
                super.onSuccess(data)
                    taskList.remove(taskList[position])
                homeAdapter.setData(taskList)
            }
        })
    }
    private fun completeTask(position: Int) {
        ServiceConnector.restInterface.updateTaskById(taskList[position]._id).enqueue(object : BaseCallBack<Task>(){
            override fun onSuccess(data: Task) {
                super.onSuccess(data)
                taskList[position].completed = data.completed
                Log.e("updated Success","${data.completed}")
                homeAdapter.setData(taskList)

            }

            override fun onFailure() {
                super.onFailure()
                Log.e("updated Failed","asd")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickDelete(position: Int) {
        deleteTask(position)
    }

    override fun onClickComplete(position: Int) {
        completeTask(position)
    }
}