package com.dicoding.asclepius.helper

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


@Suppress("DEPRECATION")
class ImageClassifierHelper(
    private val threshold: Float = 0.1f,
    private val modelName: String = "cancer_classification.tflite",
    private val context: Context,
    private val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    /***
     * Prepare and initialize the image classifier
     * using the asset file model and option that has been defined.
     */
    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    /***
     * Classify the image
     */
    fun classifyStaticImage(context: Context, imageUri: Uri) {
        // If there is no image classifier, initialize one
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        //Build ImageProcessor for pre processing the image to meet the requirement
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .add(NormalizeOp(0f, 1f))
            .build()

        //Build ImageProcessingOptions that specifies the processing actions
        //that should be applied
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .build()

        // Convert the imageUri to bitmap
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)

        // Convert the image to TensorImage
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        //Inference/classification time!!! :>
        var inferenceTime = SystemClock.uptimeMillis()
        val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        if (results == null) {
            classifierListener?.onError(context.getString(R.string.classification_failed))
        } else {
            // Extract the highest-scoring category
            val highestCategory = results[0].categories.maxByOrNull { it.score }

            // Check if highestCategory is not null and pass it as a single-item list to the listener
            if (highestCategory != null) {
                classifierListener?.onResults(highestCategory, inferenceTime)

            } else {
                classifierListener?.onError(context.getString(R.string.classification_failed))
            }
        }
    }

    /***
     * Interface ClassifierListener
     * to handle the result of the classifier
     */
    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            result: Category?,
            inferenceTime: Long
        )
    }

}