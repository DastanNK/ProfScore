package com.dastan.profscore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.profscore.data.ProfData
import com.dastan.profscore.data.Tag
import com.dastan.profscore.data.UserComments
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await


const val USERS_COLLECTION = "users"
const val PROFESSOR_COLLECTION = "professor"

@HiltViewModel
class ViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {
    var inProgress = mutableStateOf(false)
    var profData = mutableStateOf<List<ProfData>>(emptyList())
    val givenProfessorData: MutableState<ProfData?> = mutableStateOf(null)
    var commentData = mutableStateOf<List<UserComments>>(emptyList())
    var threeCommentData = mutableStateOf<List<UserComments>>(emptyList())
    val queryText = MutableStateFlow("")
    private val queryInput = Channel<String>(Channel.CONFLATED)
    val quantityProfessor = mutableStateOf(10)
    val moreProf = mutableStateOf(false)
    val allProfDataSimilar = mutableStateOf<Map<String, List<ProfData>>>(emptyMap())
    private val findProf = mutableStateOf<List<UserComments>>(emptyList())
    private val findSecondProf = mutableStateOf<List<UserComments>>(emptyList())
    val profDataSimilar = mutableStateOf<Map<String, List<ProfData>>>(emptyMap())
    private val _quality = MutableStateFlow(0)
    val quality: StateFlow<Int> = _quality.asStateFlow()


    init {
        retrieveProfessor()
        readProfessor(quantityProfessor.value)
    }

    fun updateQuality(newQuality: Int) {
        _quality.value = newQuality
    }

    private fun retrieveProfessor() {
        viewModelScope.launch(Dispatchers.IO) {
            queryInput.receiveAsFlow()
                .filter {
                    validateQuery(it)
                }
                .debounce(500)
                .collect { query ->
                    onSearch(query)
                }
        }
    }

    fun onReadUpdate(quantityProfessor: Int) {
        readProfessor(quantityProfessor)
    }

    private fun readProfessor(quantityProfessor: Int) {
        viewModelScope.launch {
            inProgress.value = true

            db.collection(PROFESSOR_COLLECTION)
                .limit(quantityProfessor.toLong())
                .get()
                .addOnSuccessListener { document ->
                    val professorData = document.toObjects<ProfData>()
                    moreProf.value = professorData.size >= quantityProfessor
                    Log.d("", professorData.size.toString())
                    profData.value = professorData
                    inProgress.value = false
                    Log.d("", "hello")
                }
                .addOnFailureListener { exception ->
                    Log.d("readProfessor", "Error getting documents: ")
                    inProgress.value = false
                }
        }

    }

    fun onQueryUpdate(input: String) {
        if (input.isEmpty()) {
            queryText.value = ""
            queryInput.trySend("")
            readProfessor(quantityProfessor.value)
        } else {
            queryText.value = input
            queryInput.trySend(input)
        }

    }

    private fun validateQuery(query: String): Boolean = query.length >= 2

    private fun onSearch(query: String) {
        viewModelScope.launch {
            inProgress.value = true
            db.collection(PROFESSOR_COLLECTION)
                .whereArrayContains("profNameLowercase", query.trim().lowercase())
                .get().addOnSuccessListener { documents ->
                    val newProfData = documents.toObjects<ProfData>()
                    //profData = listOf()
                    profData.value = newProfData
                    Log.d("", "hello")
                }.await()
            db.collection(PROFESSOR_COLLECTION)
                .whereArrayContains("departmentLowercase", query.trim().lowercase())
                .get().addOnSuccessListener { documents ->
                    val newProfDataTwo = documents.toObjects<ProfData>()
                    newProfDataTwo.forEach { newProfData ->
                        if (profData.value.contains(newProfData)) {
                            //
                        } else {
                            profData.value += newProfData
                            Log.d("onSearch", "hello")
                        }
                    }
                }.await()
            db.collection(PROFESSOR_COLLECTION)
                .whereArrayContains("courses", query.trim().lowercase())
                .get().addOnSuccessListener { documents ->
                    val newProfDataThree = documents.toObjects<ProfData>()
                    newProfDataThree.forEach { newProfData ->
                        if (profData.value.contains(newProfData)) {
                            //
                        } else {
                            profData.value += newProfData
                            Log.d("onSearch", "hello")
                        }

                    }
                }.await()
            inProgress.value = false

        }
    }

    fun <T> List<T>.permutations(): List<List<T>> {
        if (this.size == 1) return listOf(this)
        val perms = mutableListOf<List<T>>()
        val subPerms = this.drop(1).permutations()
        val first = this.first()
        subPerms.forEach { perm ->
            for (i in 0..perm.size) {
                perms.add(perm.toMutableList().apply { add(i, first) })
            }
        }
        return perms
    }


    fun addProf(profName: String, department: String) {
        /*viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val profNameLowercase = profName.trim().lowercase().split(" ")
            val profNameSubstrings = mutableListOf<String>()

            val profNamePermutations = profNameLowercase.permutations()
            profNamePermutations.forEach { permutation ->
                // Generate substrings for each permutation
                val permutationJoined = permutation.joinToString(" ")
                for (i in 1..permutationJoined.length) {
                    profNameSubstrings.add(permutationJoined.substring(0, i))
                }
            }
            val departmentLowercase = department.trim().lowercase().split(" ")
            val departmentSubstrings = mutableListOf<String>()
            val departmentPermutations = departmentLowercase.permutations()
            departmentPermutations.forEach { permutation ->
                // Generate substrings for each permutation
                val permutationJoined = permutation.joinToString(" ")
                for (i in 1..permutationJoined.length) {
                    departmentSubstrings.add(permutationJoined.substring(0, i))
                }
            }
            val newProf = ProfData(
                id = id,
                profName = profName,
                profNameLowercase = profNameSubstrings,
                department = department,
                departmentLowercase = departmentSubstrings
            )
            db.collection(PROFESSOR_COLLECTION).document(id).set(newProf).addOnSuccessListener {
                //readProfessor(quantityProfessor.value)
                Log.d("addProf","hi")
            }
        }*/

    }

    fun retrieveGivenProf(profId: String) {
        viewModelScope.launch {
            db.collection(PROFESSOR_COLLECTION)
                .document(profId)//you can write document(profId) and then delete firstOrNull()
                .get().addOnSuccessListener { document ->
                    val oneProf = document.toObject<ProfData>()
                    //Log.d("", oneProf?.fiveDifficulty.toString())
                    givenProfessorData.value = oneProf
                    Log.d("retrieveGivenProf", "hello")

                }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addUserComment(
        profId: String,
        gpa: String,
        wouldTakeAgain: Boolean,
        difficulty: Int,
        quality: Int,
        attendance: Boolean,
        course: String,
        tags: List<Tag>,
        comments: String
    ) {
        viewModelScope.launch {
            inProgress.value = true
            val commentId = UUID.randomUUID().toString()
            val date = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
            val formattedDate = date.format(formatter)
            val userComments = UserComments(
                id = commentId,
                profId = profId,
                gpa = gpa,
                wouldTakeAgain = wouldTakeAgain,
                difficulty = difficulty,
                quality = quality,
                tags = tags,
                attendance = attendance,
                date = formattedDate,
                course = course,
                comments = comments
            )
            db.collection(USERS_COLLECTION).document(commentId).set(userComments).addOnSuccessListener {
                readThreeComments(profId)
            }
            val courses = course.trim().lowercase().split(" ")
            val coursesSubstrings = mutableListOf<String>()
            val coursesPermutations = courses.permutations()
            coursesPermutations.forEach { permutation ->
                // Generate substrings for each permutation
                val permutationJoined = permutation.joinToString(" ")
                for (i in 1..permutationJoined.length) {
                    coursesSubstrings.add(permutationJoined.substring(0, i))
                }
            }
            Log.d("", coursesSubstrings.toString())
            db.collection(PROFESSOR_COLLECTION).document(profId)
                .update("courses", FieldValue.arrayUnion(*coursesSubstrings.toTypedArray()))
            inProgress.value = false
            averageQuantity(profId)
            Log.d("addUserComment", "hi")
        }

    }

    fun readAllComments(profId: String) {
        viewModelScope.launch {
            inProgress.value = true
            db.collection(USERS_COLLECTION).whereEqualTo("profId", profId).get().addOnSuccessListener { document ->
                val newCommentData = document.toObjects<UserComments>()
                commentData.value = newCommentData.sortedByDescending { it.helpful?.minus(it.notHelpful ?: 0) }
                val allCourse = mutableStateOf<List<String>>(emptyList())
                commentData.value.forEach { commentData ->
                    if (commentData.course?.isNotEmpty() == true) {
                        allCourse.value += commentData.course
                    }
                }
                //Log.d("", allCourses.value.toString())
                retrieveSimilarProf(allCourse.value, profId)
                inProgress.value = false
                Log.d("readAllComments", "hello")
            }
            //averageQuantity(profId)
        }
    }

    fun readThreeComments(profId: String) {
        viewModelScope.launch {
            inProgress.value = true
            db.collection(USERS_COLLECTION).whereEqualTo("profId", profId).limit(3).get()
                .addOnSuccessListener { document ->
                    val newCommentData = document.toObjects<UserComments>()
                    threeCommentData.value = newCommentData.sortedByDescending { it.helpful?.minus(it.notHelpful ?: 0) }
                    val allCourses = mutableStateOf<List<String>>(emptyList())
                    threeCommentData.value.forEach { threeCommentData ->
                        if (threeCommentData.course?.isNotEmpty() == true) {
                            allCourses.value += threeCommentData.course
                        }
                    }
                    //Log.d("", allCourses.value.toString())
                    retrieveFiveSimilarProf(allCourses.value, profId)
                    inProgress.value = false
                    Log.d("readThreeComments", "hello")
                }
        }

    }

    fun addHelpfulness(id: String, helpful: Int, notHelpful: Int, profId: String) {
        viewModelScope.launch {
            val updates = mapOf(
                "helpful" to helpful,
                "notHelpful" to notHelpful
            )

            db.collection(USERS_COLLECTION).document(id).update(updates)
                .addOnSuccessListener {
                    //readAllComments(profId)
                    Log.d("addHelpfulness", "hi")
                }
        }
    }

    private fun averageQuantity(profId: String) {
        viewModelScope.launch {
            var averageDifficulty = 0.0
            var averageQuality = 0.0
            var averageWouldTakeAgain = 0.0
            var oneDifficulty = 0
            var twoDifficulty = 0
            var threeDifficulty = 0
            var fourDifficulty = 0
            var fiveDifficulty = 0

            db.collection(USERS_COLLECTION).whereEqualTo("profId", profId).get().addOnSuccessListener { document ->

                val calcProfData = document.toObjects<UserComments>()
                if (calcProfData.size != 0) {
                    calcProfData.forEach { calcProfData ->
                        averageDifficulty += calcProfData.difficulty ?: 0
                        if (calcProfData.difficulty == 1) {
                            oneDifficulty++
                        } else if (calcProfData.difficulty == 2) {
                            twoDifficulty++
                        } else if (calcProfData.difficulty == 3) {
                            threeDifficulty++
                        } else if (calcProfData.difficulty == 4) {
                            fourDifficulty++
                        } else if (calcProfData.difficulty == 5) {
                            fiveDifficulty++
                        }
                        averageQuality += calcProfData.quality ?: 0
                        averageWouldTakeAgain += if (calcProfData.wouldTakeAgain == true) 100 else 0
                    }
                    averageDifficulty /= calcProfData.size.toDouble()
                    averageQuality /= calcProfData.size.toDouble()
                    averageWouldTakeAgain /= calcProfData.size.toDouble()
                    averageDifficulty = Math.round(averageDifficulty * 10) / 10.0
                    averageQuality = Math.round(averageQuality * 10) / 10.0
                    averageWouldTakeAgain = Math.round(averageWouldTakeAgain * 10) / 10.0
                    db.collection(PROFESSOR_COLLECTION).document(profId).update(
                        mapOf(
                            "averageDifficulty" to averageDifficulty,
                            "averageQuality" to averageQuality,
                            "averageWouldTakeAgain" to averageWouldTakeAgain,
                            "quantity" to calcProfData.size,
                            "oneDifficulty" to oneDifficulty,
                            "twoDifficulty" to twoDifficulty,
                            "threeDifficulty" to threeDifficulty,
                            "fourDifficulty" to fourDifficulty,
                            "fiveDifficulty" to fiveDifficulty
                        )
                    )
                    Log.d("averageQuantity", "hi")
                }

            }
            //retrieveGivenProf(profId)
            //readProfessor(quantityProfessor.value)
        }

    }


    private fun retrieveFiveSimilarProf(course: List<String>? = null, profId: String) {
        viewModelScope.launch {
            inProgress.value = true
            findProf.value = emptyList()
            profDataSimilar.value = emptyMap() // Reset hashmap
            course?.forEach { courses ->
                db.collection(USERS_COLLECTION)
                    .whereEqualTo("course", courses).limit(5)
                    .get().addOnSuccessListener { documents ->
                        val newProfDataFour = documents.toObjects<UserComments>()
                        findProf.value = findProf.value + newProfDataFour
                        findProf.value.forEach { findProf ->
                            if (findProf.profId.toString() != profId) {
                                db.collection(PROFESSOR_COLLECTION).document(findProf.profId.toString())
                                    .get().addOnSuccessListener { document ->
                                        val newProfDataFive = document.toObject<ProfData>()
                                        if (newProfDataFive != null) {
                                            val currentList =
                                                profDataSimilar.value[courses]?.toMutableList() ?: mutableListOf()
                                            if (!currentList.contains(newProfDataFive)) {
                                                currentList.add(newProfDataFive)
                                                profDataSimilar.value = profDataSimilar.value + (courses to currentList)
                                                Log.d("retrieveFiveSimilarProf", "hello")
                                            }
                                        }
                                    }
                            }
                        }
                    }
            }
            inProgress.value = false
        }
    }


    private fun retrieveSimilarProf(course: List<String>? = null, profId: String) {
        viewModelScope.launch {
            inProgress.value = true
            findSecondProf.value = emptyList()
            allProfDataSimilar.value = emptyMap()

            course?.forEach { courses ->
                db.collection(USERS_COLLECTION)
                    .whereEqualTo("course", courses)
                    .get().addOnSuccessListener { documents ->
                        val newProfDataSix = documents.toObjects<UserComments>()
                        findSecondProf.value = findSecondProf.value + newProfDataSix

                        findSecondProf.value.forEach { findSecondProf ->
                            if (findSecondProf.profId.toString() != profId) {
                                db.collection(PROFESSOR_COLLECTION)
                                    .document(findSecondProf.profId.toString())
                                    .get().addOnSuccessListener { document ->
                                        val newProfDataFive = document.toObject<ProfData>()
                                        if (newProfDataFive != null) {
                                            val currentList =
                                                allProfDataSimilar.value[courses]?.toMutableList() ?: mutableListOf()
                                            if (!currentList.contains(newProfDataFive)) {
                                                currentList.add(newProfDataFive)
                                                allProfDataSimilar.value =
                                                    allProfDataSimilar.value + (courses to currentList)
                                                Log.d("retrieveSimilarProf", "hello")
                                            }
                                        }
                                    }
                            }
                        }
                    }
            }

            inProgress.value = false
        }
    }


}