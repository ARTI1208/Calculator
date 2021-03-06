package ru.art2000.calculator.view.unit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.art2000.calculator.databinding.UnitFragBinding
import ru.art2000.extensions.fragments.IReplaceableFragment

class PowerfulUnitPageFragment : BaseUnitPageFragment<UnitFragBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragBinding {
        return UnitFragBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        binding.unitRv.layoutManager = LinearLayoutManager(requireContext())
        binding.unitRv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        binding.unitRv.adapter = UnitListAdapter(
                requireContext(), viewLifecycleOwner, items, true
        )
    }

    override fun onShown(previousReplaceable: IReplaceableFragment?) {
        (binding.unitRv.adapter as UnitListAdapter).requestFocusForCurrent()
    }
}