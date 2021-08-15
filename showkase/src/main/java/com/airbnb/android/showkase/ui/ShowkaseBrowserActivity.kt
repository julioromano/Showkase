package com.airbnb.android.showkase.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import com.airbnb.android.showkase.exceptions.ShowkaseException
import com.airbnb.android.showkase.impressionlogging.TestScreen
import com.airbnb.android.showkase.models.ShowkaseBrowserScreenMetadata
import com.airbnb.android.showkase.models.ShowkaseProvider
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata

/**
 * The activity that's responsible for showing all the UI elements that were annotated
 * with the Showkase related annotations.
 */
class ShowkaseBrowserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val classKey = intent.extras?.getString(SHOWKASE_ROOT_MODULE_KEY) ?: throw ShowkaseException(
            "Missing key in bundle. Please start this activity by using the intent returned by " +
                    "the ShowkaseBrowserActivity.getIntent() method."
        )
        setContent {
            TestScreen()
//            val (
//                groupedComponentsList,
//                groupedColorsList,
//                groupedTypographyList
//            ) = getShowkaseProviderElements(classKey)
//
//            val showkaseBrowserScreenMetadata =
//                remember { mutableStateOf(ShowkaseBrowserScreenMetadata()) }
//            when {
//                groupedComponentsList.isNotEmpty() || groupedColorsList.isNotEmpty() ||
//                        groupedTypographyList.isNotEmpty() -> {
//                    ShowkaseBrowserApp(
//                        groupedComponentsList.groupBy { it.group },
//                        groupedColorsList.groupBy { it.colorGroup },
//                        groupedTypographyList.groupBy { it.typographyGroup },
//                        showkaseBrowserScreenMetadata)
//                }
//                else -> {
//                    ShowkaseErrorScreen(
//                        errorText = "There were no elements that were annotated with either " +
//                                "@ShowkaseComposable, @ShowkaseTypography or @ShowkaseColor. If " +
//                                "you think this is a mistake, file an issue at " +
//                                "https://github.com/airbnb/Showkase/issues"
//                    )
//                }
//            }
        }
    }

    private fun getShowkaseProviderElements(
        classKey: String
    ): ShowkaseElementsMetadata {
        return try {
            val showkaseComponentProvider =
                Class.forName("$classKey$AUTOGEN_CLASS_NAME").newInstance()
            
            val showkaseMetadata = (showkaseComponentProvider as ShowkaseProvider).metadata()

            ShowkaseElementsMetadata(
                componentList = showkaseMetadata.componentList,
                colorList = showkaseMetadata.colorList,
                typographyList = showkaseMetadata.typographyList
            )
        } catch (exception: ClassNotFoundException) {
            ShowkaseElementsMetadata()
        }
    }

    companion object {
        private const val SHOWKASE_ROOT_MODULE_KEY = "SHOWKASE_ROOT_MODULE"
        private const val AUTOGEN_CLASS_NAME = "Codegen"

        /**
         * Returns the intent that the users of this library need to use for starting the
         * Showkase browser activity. Please make sure to use this instead of starting the
         * activity directly as it sets the right value in the bundle in order for the activity
         * to start correctly.
         * 
         * @param context Android context
         * @param rootModuleCanonicalName The canonical name of the implementation of 
         * ShowkaseRootModule.
         */
        fun getIntent(context: Context, rootModuleCanonicalName: String) =
            Intent(context, ShowkaseBrowserActivity::class.java).apply {
                putExtra(SHOWKASE_ROOT_MODULE_KEY, rootModuleCanonicalName)
            }
    }
}
