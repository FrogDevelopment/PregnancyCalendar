package fr.frogdevelopment.pregnancycalendar.ui.contraction

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.data.Contraction
import fr.frogdevelopment.pregnancycalendar.ui.chrono.ChronoActivity
import fr.frogdevelopment.pregnancycalendar.ui.contraction.SwipeToDelete.DeleteListener
import fr.frogdevelopment.pregnancycalendar.utils.DateLabelUtils
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

class ContractionFragment : Fragment(), DeleteListener {

    private lateinit var mContractionViewModel: ContractionViewModel
    private lateinit var mAverageInterval: TextView
    private lateinit var mAverageDuration: TextView
    private lateinit var mAdapter: ContractionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContractionViewModel = ViewModelProvider(this).get(ContractionViewModel::class.java)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.contraction_fragment, container, false)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        mAverageInterval = rootView.findViewById(R.id.average_interval_value)
        mAverageDuration = rootView.findViewById(R.id.average_duration_value)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.chrono_list)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = ContractionAdapter(requireActivity())
        recyclerView.adapter = mAdapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(requireContext(), this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        mContractionViewModel.allContractions.observe(viewLifecycleOwner, Observer { contractions: List<Contraction> ->
            mAdapter.setContractions(contractions)
            computeStats(contractions)
        })

        rootView.findViewById<View>(R.id.chrono_button).setOnClickListener { start() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contraction, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.contraction_delete_all_title)
                    .setMessage(R.string.contraction_delete_all_confirmation)
                    .setPositiveButton(R.string.contraction_delete_all_positive_button) { _: DialogInterface?, _: Int -> mContractionViewModel.deleteAll() }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            return true
        }
        return false
    }

    override fun onDelete(viewHolder: ViewHolder) {
        val item = mAdapter.getAtPosition(viewHolder.adapterPosition)
        if (item != null) {
            mContractionViewModel.delete(item)
            Snackbar.make(requireView(), R.string.contraction_delete_all_deleted, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.contraction_delete_all_undo) {
                        item.id = null
                        mContractionViewModel.insert(item)
                    }
                    .setActionTextColor(Color.RED)
                    .show()
        }
    }

    private fun computeStats(contractions: List<Contraction>) {
        var averageInterval: String? = null
        var averageDuration: String? = null

        val itemCount = contractions.size
        if (itemCount > 0) {
            val intervals: MutableList<Long> = ArrayList()
            val durations: MutableList<Long> = ArrayList()
            var last: Contraction? = null
            var previous: Contraction? = null
            var contraction: Contraction
            for (i in itemCount - 1 downTo 0) {
                contraction = contractions[i]

                if (last == null) {
                    last = contraction
                    previous = contraction
                    durations.add(contraction.duration!!)
                    continue
                }

                intervals.add(ChronoUnit.MILLIS.between(contraction.dateTime!!.plus(contraction.duration!!, ChronoUnit.MILLIS), previous!!.dateTime))
                durations.add(contraction.duration!!)
                previous = contraction
            }
            averageInterval = DateLabelUtils.millisecondsToLabel(requireContext(), intervals.stream().collect(Collectors.averagingLong { d: Long? -> d!! }).toLong())
            averageDuration = DateLabelUtils.millisecondsToLabel(requireContext(), durations.stream().collect(Collectors.averagingLong { d: Long? -> d!! }).toLong())
        }
        mAverageInterval.text = averageInterval
        mAverageDuration.text = averageDuration
    }

    private fun start() {
        val intent = Intent(requireContext(), ChronoActivity::class.java)
        val activityNavigator = ActivityNavigator(requireContext())
        val destination = activityNavigator
                .createDestination()
                .setIntent(intent)
        activityNavigator.navigate(destination, null, null, null)
    }
}