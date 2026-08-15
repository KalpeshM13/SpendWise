package dev.kalpeshmore.spendwise.ui.profile

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.data.firebase.FirebaseService
import dev.kalpeshmore.spendwise.data.models.UserProfile
import dev.kalpeshmore.spendwise.databinding.FragmentEditProfileBinding
import dev.kalpeshmore.spendwise.ui.auth.ChangePasswordFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val firebaseService = FirebaseService()
    private var currentAvatar = "girl"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile from Firestore
        CoroutineScope(Dispatchers.IO).launch {
            val profile = firebaseService.getUserProfile()
            withContext(Dispatchers.Main) {
                if (profile != null) {
                    binding.profileUsername.setText(profile.name)
                    binding.profileEmail.setText(profile.email)
                    currentAvatar = profile.avatar
                    updateAvatarImage()
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val name = binding.profileUsername.text.toString().trim()
            val email = binding.profileEmail.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val existing = firebaseService.getUserProfile()
                val updated = UserProfile(
                    name = name,
                    email = email,
                    phone = existing?.phone ?: "",
                    avatar = currentAvatar
                )
                firebaseService.saveUserProfile(updated)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }

        binding.changePassword.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ChangePasswordFragment().show(parentFragmentManager, "ChangePasswordFragment")
        }

        binding.btnEditAvatar.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showAvatarPickerDialog()
        }
    }

    private fun showAvatarPickerDialog() {
        val options = arrayOf("Girl", "Boy")
        val currentIndex = if (currentAvatar == "girl") 0 else 1

        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog
        )
            .setTitle("Choose Avatar")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                currentAvatar = if (which == 0) "girl" else "boy"
                updateAvatarImage()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateAvatarImage() {
        val drawableRes = if (currentAvatar == "boy") {
            R.drawable.boy_avatar
        } else {
            R.drawable.girl_avatar
        }
        binding.profileImage.setImageResource(drawableRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}