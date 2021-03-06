package com.rohitthebest.projectplanner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.http.SslError
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.cardview.widget.CardView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.db.entity.*
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.*

data class ClassForAddingProject(
        val context: Context,
        val projectViewModel: ProjectViewModel
)

private const val TAG = "AddingToProjectFunction"

class ProjectHelperFunctions {

    companion object {

        /**[START OF FEATURE]*/

        @SuppressLint("CheckResult")
        fun showBottomSheetDialogForAddingFeature(
                classForAddingProject: ClassForAddingProject,
                project: Project,
                feature: Feature? = null,
                position: Int = 0
        ) {

            MaterialDialog(classForAddingProject.context, BottomSheet()).show {

                title(text = "Add New Feature")

                customView(
                        R.layout.add_feature_layout,
                        scrollable = true
                )

                if (feature != null) {

                    title(text = "Edit Feature")

                    setInitialFeatureValues(getCustomView(), feature)
                }

                val featureName = getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).editText
                val featureDescription = getCustomView().findViewById<TextInputLayout>(R.id.featureDescriptionET).editText
                val featureImplementation = getCustomView().findViewById<TextInputLayout>(R.id.featureInplementationET).editText

                //adding text watcher on feature name editText
                featureName?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                        if (s?.isEmpty()!!) {

                            getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error =
                                "This is a mandatory field!!"
                        } else {

                            getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error =
                                null
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                var isDuplicateFeatureExists = false

                positiveButton(text = "Save") {

                    if (featureName?.text.toString().trim().isEmpty()) {

                        showToast(
                            classForAddingProject.context,
                            "Cannot add empty feature!!"
                        )
                    } else {

                        val featureToBeAdded = Feature(
                            featureName?.text.toString().trim(),
                            featureDescription?.text.toString().trim(),
                            featureImplementation?.text.toString().trim()
                        )

                        if (project.features.size != 0) {

                            val featureNameList = project.features.map { f ->

                                f.name.toLowerCase(Locale.ROOT)
                            }

                            if (feature == null) {

                                if (featureNameList.contains(featureToBeAdded.name.toLowerCase(Locale.ROOT))) {

                                    isDuplicateFeatureExists = true
                                }
                            } else {

                                // if feature is in edit mode then checking for duplicates excluding
                                // the passes feature name
                                for (f in featureNameList) {

                                    if (f == featureToBeAdded.name.toLowerCase(Locale.ROOT)) {

                                        if (f == feature.name.toLowerCase(Locale.ROOT)) {

                                            continue
                                        } else {

                                            isDuplicateFeatureExists = true
                                            break
                                        }
                                    }
                                }
                            }
                        }

                        if (!isDuplicateFeatureExists) {

                            if (feature == null) {

                                //add

                                project.features.add(position, featureToBeAdded)

                                Log.d(
                                    TAG,
                                    "openFeatureBottomSheetDialog: feature added at position $position " +
                                            ": $featureToBeAdded"
                                )
                            } else {

                                //edit

                                project.features[position] = featureToBeAdded

                                Log.d(
                                    TAG,
                                    "openFeatureBottomSheetDialog: feature edited at position $position " +
                                            ": $featureToBeAdded"
                                )
                            }
                        } else {
                            "This feature name already exits!!!".showToasty(
                                    classForAddingProject.context,
                                    ToastyType.ERROR,
                                    true
                            )
                        }
                        it.dismiss()
                    }
                }

            }.negativeButton(text = "Cancel") {

                it.dismiss()
            }.setOnDismissListener {

                classForAddingProject.projectViewModel.updateProject(project)
                //setUpFeaturesRecyclerView()
            }
        }

        private fun setInitialFeatureValues(customView: View, feature: Feature) {

            val featureName = customView.findViewById<TextInputLayout>(R.id.featureNameET).editText
            val featureDescription = customView.findViewById<TextInputLayout>(R.id.featureDescriptionET).editText
            val featureImplementation = customView.findViewById<TextInputLayout>(R.id.featureInplementationET).editText

            featureName?.setText(feature.name)
            featureDescription?.setText(feature.description)
            featureImplementation?.setText(feature.implementation)

        }


        /**[END OF FEATURE]**/


        /**[START OF SKILL]**/

        fun showDialogForAddingSkills(
                classForAddingProject: ClassForAddingProject,
                skill: String? = null,
                project: Project,
                position: Int = 0
        ) {

            MaterialDialog(classForAddingProject.context).show {

                title(text = "Add Skill")

                var isDuplicateSkillExits = false


                if (skill != null) {

                    title(text = "Edit Skill")

                    input(
                        hint = "Edit Skill", prefill = skill,
                        inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    ) { _, charSequence ->

                        if (charSequence.toString().trim().isEmpty()) {

                            showToast(classForAddingProject.context, "Cannot edit empty skill!!!")
                        } else {

                            if (project.skillsRequired.size != 0) {

                                if (project.skillsRequired.contains(
                                        charSequence.toString().trim()
                                    )
                                ) {

                                    isDuplicateSkillExits = true
                                }
                            }

                            if (!isDuplicateSkillExits) {

                                project.skillsRequired[position] = charSequence.toString().trim()
                                Log.d(
                                    TAG,
                                    "showDialogForAddingSkills: Skill edited at position $position Skill = ${
                                        charSequence.toString().trim()
                                    }"
                                )
                            } else {

                                "This Skill already exits!!".showToasty(
                                    classForAddingProject.context,
                                    ToastyType.ERROR,
                                    true
                                )
                            }
                        }
                    }
                } else {

                    input(hint = "Add skill", inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) { _, charSequence ->

                        if (charSequence.toString().trim().isEmpty()) {

                            showToast(classForAddingProject.context, "Cannot add empty skill!!!")
                        } else {

                            if (project.skillsRequired.size != 0) {

                                if (project.skillsRequired.contains(
                                        charSequence.toString().trim()
                                    )
                                ) {

                                    isDuplicateSkillExits = true
                                }
                            }

                            if (!isDuplicateSkillExits) {

                                project.skillsRequired.add(position, charSequence.toString().trim())

                                Log.d(
                                    TAG,
                                    "showDialogForAddingSkills: Skill Added at position $position Skill = ${
                                        charSequence.toString().trim()
                                    }"
                                )
                            } else {

                                "This Skill already exits!!".showToasty(
                                    classForAddingProject.context,
                                    ToastyType.ERROR,
                                    true
                                )
                            }
                        }
                    }
                }


            }.negativeButton(text = "Cancel") {

                it.dismiss()
            }.setOnDismissListener {

                classForAddingProject.projectViewModel.updateProject(project)
            }
        }

        /**[END OF SKILL]**/


        /**[START OF TECHNOLOGY]**/

        fun showBottomSheetDialogForAddingTechnology(
                classForAddingProject: ClassForAddingProject,
                project: Project,
                technology: Technology? = null,
                position: Int = 0
        ) {

            MaterialDialog(classForAddingProject.context, BottomSheet()).show {

                title(text = "Add technology used")

                customView(
                        R.layout.add_technology_layout,
                        scrollable = true
                )

                val techName = getCustomView().findViewById<TextInputLayout>(R.id.techNameET).editText
                val techTextColorBtn = getCustomView().findViewById<Button>(R.id.techTextColorBtn)
                val techBackgroundColorBtn =
                        getCustomView().findViewById<Button>(R.id.techBackgroundColorBtn)
                val previewText = getCustomView().findViewById<TextView>(R.id.previewTechTV)
                val previewBackgroundColor = getCustomView().findViewById<CardView>(R.id.previewTechCV)

                if (technology != null) {

                    title(text = "Edit")

                    initializeTechnologyField(getCustomView(), technology)
                }

                techName?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (s?.isEmpty()!!) {

                            getCustomView().findViewById<TextInputLayout>(R.id.techNameET).error =
                                    "Mandatory field!!"
                        } else {

                            getCustomView().findViewById<TextInputLayout>(R.id.techNameET).error = null
                        }

                        previewText.text = s.toString().trim()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                var mDefaultTextColor: Int = if (technology != null) {

                    if (technology.textColor == null) {

                        Color.WHITE
                    } else {

                        technology.textColor!!
                    }

                } else {

                    Color.WHITE
                }
                var mDefaultBackgroundColor = if (technology != null) {
                    technology.backgroundColor ?: Color.parseColor("#FF6E40")

                } else {

                    Color.parseColor("#FF6E40")
                }

                techTextColorBtn.setOnClickListener {

                    AmbilWarnaDialog(
                        classForAddingProject.context,
                        mDefaultTextColor,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {}

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                                mDefaultTextColor = color

                                showToast(
                                    classForAddingProject.context,
                                    mDefaultTextColor.convertToHexString()
                                )

                                previewText.setTextColor(mDefaultTextColor)
                                techTextColorBtn.setBackgroundColor(mDefaultTextColor)
                            }
                        }
                    ).show()

                }

                techBackgroundColorBtn.setOnClickListener {

                    AmbilWarnaDialog(
                        classForAddingProject.context,
                        mDefaultBackgroundColor,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {}

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                                mDefaultBackgroundColor = color
                                showToast(classForAddingProject.context, color.toString())

                                previewBackgroundColor.setCardBackgroundColor(
                                    mDefaultBackgroundColor
                                )
                                techBackgroundColorBtn.setBackgroundColor(mDefaultBackgroundColor)
                            }
                        }
                    ).show()
                }

                positiveButton(text = "Save") {

                    if (techName?.text.toString().trim().isEmpty()) {

                        showToast(classForAddingProject.context, "Cannot add empty technology...")
                    } else {

                        val technologyForAdding = Technology(
                            techName?.text.toString().trim(),
                            mDefaultBackgroundColor,
                            mDefaultTextColor
                        )

                        var isDuplicateTechnologyExists = false

                        if (project.technologyUsed.size != 0) {

                            val technologyNameList = project.technologyUsed.map {

                                it.name.toLowerCase(Locale.ROOT)
                            }

                            if (technology == null) {
                                if (technologyNameList.contains(technologyForAdding.name.toLowerCase(Locale.ROOT))) {

                                    isDuplicateTechnologyExists = true
                                }
                            } else {

                                //for edit mode

                                for (t in technologyNameList) {

                                    if (t == technologyForAdding.name.toLowerCase(Locale.ROOT)) {

                                        if (t == technology.name.toLowerCase(Locale.ROOT)) {

                                            continue
                                        } else {

                                            isDuplicateTechnologyExists = true
                                            break
                                        }
                                    }
                                }
                            }
                        }

                        if (!isDuplicateTechnologyExists) {
                            if (technology == null) {

                                //add
                                project.technologyUsed.add(position, technologyForAdding)

                                Log.d(
                                    TAG,
                                    "showBottomSheetDialogForAddingTechnology: technology Added at position $position : $technologyForAdding"
                                )

                            } else {

                                //edit
                                project.technologyUsed[position] = technologyForAdding

                                Log.d(
                                    TAG,
                                    "showBottomSheetDialogForAddingTechnology: technology edited at position $position"
                                )
                            }
                        } else {

                            "This technology already exists!!!".showToasty(
                                classForAddingProject.context,
                                ToastyType.ERROR,
                                true
                            )
                        }

                    }
                }

            }.negativeButton(text = "Cancel") {

                it.dismiss()
            }.setOnDismissListener {

                classForAddingProject.projectViewModel.updateProject(project)
            }
        }

        private fun initializeTechnologyField(customView: View, technology: Technology) {

            val techName = customView.findViewById<TextInputLayout>(R.id.techNameET).editText
            val techTextColorBtn = customView.findViewById<Button>(R.id.techTextColorBtn)
            val techBackgroundColorBtn = customView.findViewById<Button>(R.id.techBackgroundColorBtn)
            val previewTextColor = customView.findViewById<TextView>(R.id.previewTechTV)
            val previewBackgroundColor = customView.findViewById<CardView>(R.id.previewTechCV)

            previewTextColor.text = technology.name
            previewTextColor.setTextColor(technology.textColor!!)
            previewBackgroundColor.setCardBackgroundColor(technology.backgroundColor!!)

            techName?.setText(technology.name)
            techTextColorBtn.setBackgroundColor(technology.textColor!!)
            techBackgroundColorBtn.setBackgroundColor(technology.backgroundColor)
        }

        /**[END OF TECHNOLOGY]**/

        /**[START OF LINK RESOURCE]**/

        //showing bottomSheet for adding link resource to the list
        fun showBottomSheetDialogForAddingLinkResource(
                classForAddingProject: ClassForAddingProject,
                project: Project,
                url: Url? = null, position: Int = 0) {

            MaterialDialog(classForAddingProject.context, BottomSheet()).show {

                title(text = "Add Link/Url")

                customView(
                        R.layout.add_link_resource_dialog_layout,
                        scrollable = true
                )

                val linkET = getCustomView().findViewById<TextInputLayout>(R.id.linkET).editText
                val linkNameET = getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).editText
                val progressBar = getCustomView().findViewById<ProgressBar>(R.id.urlProgressBar)
                val webView = getCustomView().findViewById<WebView>(R.id.urlWebView)
                val finAutomatically = getCustomView().findViewById<Button>(R.id.findUrlNameAutomatically)

                initWebView(webView)
                setWebChromeClient(webView, progressBar, linkNameET!!)

                if (url != null) {

                    initializeUrlFields(getCustomView(), url)
                }

                linkET?.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (s?.isEmpty()!!) {

                            getCustomView().findViewById<TextInputLayout>(R.id.linkET).error =
                                    Constants.EDIT_TEXT_EMPTY_MESSAGE
                        } else {

                            getCustomView().findViewById<TextInputLayout>(R.id.linkET).error = null
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                linkNameET.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (s?.isEmpty()!!) {

                            getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).error =
                                    Constants.EDIT_TEXT_EMPTY_MESSAGE
                        } else {

                            getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).error = null
                            webView.stopLoading()
                            progressBar.hide()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                finAutomatically.setOnClickListener {

                    if (linkET?.text.toString().trim().isNotEmpty()) {

                        if (isInternetAvailable(classForAddingProject.context)) {

                            progressBar.show()
                            webView.loadUrl(linkET?.text.toString().trim())
                        } else {

                            showNoInternetMessage(
                                    classForAddingProject.context
                            )
                        }
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.linkET)
                                .error = Constants.EDIT_TEXT_EMPTY_MESSAGE
                    }
                }

                positiveButton(text = "Save") {

                    if (
                            linkET?.text.toString().trim().isEmpty() ||
                            linkNameET.text.toString().trim().isEmpty()
                    ) {

                        showToast(classForAddingProject.context, "Cannot add empty url!!!")
                    } else {

                        val urlToBeAdded = Url(
                                linkNameET.text.toString().trim(),
                                linkET?.text.toString().trim()
                        )

                        var isDuplicateLinkExits = false

                        if (project.resources?.urls?.size != 0) {

                            val linkUrlList = project.resources?.urls?.map {

                                it.url.toLowerCase(Locale.ROOT)
                            }

                            if (url == null) {

                                if (linkUrlList?.contains(urlToBeAdded.url.toLowerCase(Locale.ROOT))!!) {

                                    isDuplicateLinkExits = true
                                }
                            } else {

                                for (u in linkUrlList!!) {

                                    if (u == urlToBeAdded.url.toLowerCase(Locale.ROOT)) {

                                        if (u == url.url.toLowerCase(Locale.ROOT)) {

                                            continue
                                        } else {

                                            isDuplicateLinkExits = true
                                            break
                                        }
                                    }
                                }
                            }
                        }

                        if (!isDuplicateLinkExits) {
                            if (url == null) {

                                //add

                                project.resources?.urls?.add(position, urlToBeAdded)

                                Log.d(
                                    TAG,
                                    "showBottomSheetDialogForAddingLinkResource: url added at position : $position"
                                )
                            } else {

                                //edit

                                project.resources?.urls?.set(position, urlToBeAdded)

                                Log.d(
                                    TAG,
                                    "showBottomSheetDialogForAddingLinkResource: url edited at position : $position"
                                )
                            }
                        } else {

                            "This link is already there".showToasty(
                                    classForAddingProject.context,
                                    ToastyType.ERROR,
                                    true
                            )
                        }

                    }
                }

                setOnDismissListener {

                    try {
                        webView.clearCache(true)
                        webView.clearHistory()
                        webView.clearSslPreferences()
                        webView.clearMatches()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    classForAddingProject.projectViewModel.updateProject(project)

                }

            }
        }

        private fun initializeUrlFields(customView: View, url: Url) {

            val linkET = customView.findViewById<TextInputLayout>(R.id.linkET).editText
            val linkNameET = customView.findViewById<TextInputLayout>(R.id.linkNameET).editText

            linkET?.setText(url.url)
            linkNameET?.setText(url.urlName)
        }

        private fun setWebChromeClient(
                webView: WebView?,
                progressBar: ProgressBar,
                linkTitleET: EditText
        ) {

            webView?.let {

                it.webChromeClient = object : WebChromeClient() {

                    override fun onProgressChanged(
                            view: WebView,
                            newProgress: Int
                    ) {
                        super.onProgressChanged(view, newProgress)

                        if (newProgress >= 60) {

                            progressBar.hide()
                        }
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)

                        linkTitleET.setText(title)
                        progressBar.hide()

                        try {

                            webView.stopLoading()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }

        }

        @SuppressLint("SetJavaScriptEnabled")
        private fun initWebView(webView: WebView?) {

            webView?.let {

                it.settings.apply {

                    javaScriptEnabled = true
                }

                it.webViewClient = object : WebViewClient() {
                    override
                    fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                    ) {
                        handler?.proceed()
                    }
                }
            }
        }


        /**[END OF LINK RESOURCE]**/


        /**[START OF COLORS]**/

        fun showBottomSheetDialogForAddingColor(
                classForAddingProject: ClassForAddingProject,
                project: Project,
                color: Colors? = null, position: Int = 0) {

            MaterialDialog(classForAddingProject.context, BottomSheet()).show {

                title(text = "Add colors")

                customView(
                        R.layout.add_color_layout,
                        scrollable = true
                )

                val colorBtn = getCustomView().findViewById<Button>(R.id.chooseColorBtn)
                val colorHexEt = getCustomView().findViewById<EditText>(R.id.colorHexET)
                val colorNameET = getCustomView().findViewById<TextInputLayout>(R.id.colorNameEt).editText

                if (color != null) {

                    //for editing

                    initializeColorFields(getCustomView(), color)
                }

                colorBtn.setOnClickListener {

                    val hexCode = if (colorHexEt.text.toString().trim().startsWith("#")) {

                        colorHexEt.text.toString().trim()
                    } else {
                        "#${colorHexEt.text.toString().trim()}"
                    }

                    colorBtn.openColorPicker(
                            classForAddingProject.context,

                            if (colorHexEt.text.toString().trim().isEmpty()) {

                                Color.parseColor(classForAddingProject.context.getString(R.string._119A17))
                            } else {

                                if (hexCode.isValidHexCode()) {

                                    Color.parseColor(hexCode)
                                } else {

                                    Color.parseColor(classForAddingProject.context.getString(R.string._119A17))
                                }
                            },
                            editText = colorHexEt
                    )
                }

                setTextWatcherInColorEditTexts(classForAddingProject.context, colorBtn, colorHexEt, getCustomView().findViewById(R.id.colorNameEt))

                positiveButton(text = "Save") {

                    if (
                            colorHexEt.text.toString().trim().isEmpty() || colorNameET?.text.toString().trim().isEmpty()
                    ) {

                        showToast(
                            classForAddingProject.context,
                            "Mandatory fields can't be empty!!!",
                            Toast.LENGTH_LONG
                        )
                    } else {

                        val hexCode = if (colorHexEt.text.toString().trim().startsWith("#")) {

                            colorHexEt.text.toString().trim()
                        } else {
                            "#${colorHexEt.text.toString().trim()}"
                        }

                        if (hexCode.isValidHexCode()) {

                            val colorToBeAdded = Colors(
                                colorNameET?.text.toString().trim(),
                                hexCode
                            )

                            var isDuplicateColorExits = false

                            if (project.colors.size != 0) {

                                val colorHexList = project.colors.map {

                                    it.colorHexCode.toLowerCase(Locale.ROOT)
                                }

                                if (color == null) {
                                    if (colorHexList.contains(
                                                    colorToBeAdded.colorHexCode.toLowerCase(
                                                            Locale.ROOT
                                                    )
                                            )
                                    ) {

                                        isDuplicateColorExits = true
                                    }
                                } else {

                                    for (c in colorHexList) {

                                        if (c == colorToBeAdded.colorHexCode.toLowerCase(Locale.ROOT)) {

                                            if (c == color.colorHexCode.toLowerCase(Locale.ROOT)) {

                                                continue
                                            } else {

                                                isDuplicateColorExits = true
                                                break
                                            }
                                        }
                                    }
                                }
                            }

                            if (!isDuplicateColorExits) {
                                if (color != null) {

                                    //edit

                                    project.colors[position] = colorToBeAdded

                                    Log.d(
                                        TAG,
                                        "showBottomSheetDialogForAddingColor: color edited at position $position Color : $colorToBeAdded"
                                    )

                                } else {

                                    //add

                                    project.colors.add(position, colorToBeAdded)

                                    Log.d(
                                        TAG,
                                        "showBottomSheetDialogForAddingColor: color added at position $position Color : $colorToBeAdded"
                                    )

                                }
                            } else {

                                "This color already exists!!!".showToasty(
                                    classForAddingProject.context,
                                    ToastyType.ERROR,
                                    true
                                )
                            }

                        } else {

                            showToast(classForAddingProject.context, "Incorrect hex code")
                        }
                    }
                }
            }.negativeButton(text = "Cancel") {

                it.dismiss()
            }.setOnDismissListener {

                classForAddingProject.projectViewModel.updateProject(project)
            }
        }

        private fun setTextWatcherInColorEditTexts(context: Context, viewToColored: View, colorHexEt: EditText?, coloNameEt: TextInputLayout?) {

            colorHexEt?.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        colorHexEt.error = Constants.EDIT_TEXT_EMPTY_MESSAGE
                    } else {

                        colorHexEt.error = null

                        Log.d(TAG, "onTextChanged: s.length = ${s.length}")

                        if (s.length in 6..7) {

                            Log.d(TAG, "onTextChanged: s.length is in range 6..7")

                            val hexCode = if (s.toString().trim().startsWith("#")) {
                                s.toString().trim()
                            } else {
                                "#${s.toString().trim()}"
                            }

                            if (hexCode.isValidHexCode()) {

                                Functions.applyColor(
                                        context,
                                        viewToColored,
                                        hexCode = hexCode
                                )
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            coloNameEt?.editText?.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        coloNameEt.error = Constants.EDIT_TEXT_EMPTY_MESSAGE
                    } else {

                        coloNameEt.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

        private fun initializeColorFields(customView: View, color: Colors) {

            val colorBtn = customView.findViewById<Button>(R.id.chooseColorBtn)
            val colorHexEt = customView.findViewById<EditText>(R.id.colorHexET)
            val colorNameET = customView.findViewById<TextInputLayout>(R.id.colorNameEt).editText

            colorBtn.setBackgroundColor(Color.parseColor(color.colorHexCode))
            colorHexEt.setText(color.colorHexCode)
            colorNameET?.setText(color.colorName)
        }

        /**[END OF COLORS]**/
    }

}