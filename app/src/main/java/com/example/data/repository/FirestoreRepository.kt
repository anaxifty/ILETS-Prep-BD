package com.example.data.repository

import android.util.Log
import com.example.data.models.ListeningAttempt
import com.example.data.models.ListeningQuestion
import com.example.data.models.ListeningQuestionType
import com.example.data.models.ListeningTest
import com.example.data.models.QuestionType
import com.example.data.models.ReadingAttempt
import com.example.data.models.ReadingQuestion
import com.example.data.models.ReadingTest
import com.example.data.models.SpeakingAttempt
import com.example.data.models.SpeakingCriterionScore
import com.example.data.models.SpeakingTask
import com.example.data.models.UserProfile
import com.example.data.models.VideoLesson
import com.example.data.models.LessonProgress
import com.example.data.models.WritingAttempt
import com.example.data.models.WritingCriterionScore
import com.example.data.models.WritingTask
import com.example.data.models.MockExam
import com.example.data.models.MockExamAttempt
import com.example.data.models.PartnerCenter
import com.example.data.models.PartnerSlot
import com.example.data.models.CenterBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRepository {

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            null
        }

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            null
        }

    // Local in-memory attempts fallback
    private val localAttempts = mutableListOf<ReadingAttempt>()
    private val _attemptsFlow = MutableStateFlow<List<ReadingAttempt>>(emptyList())
    val attemptsFlow: Flow<List<ReadingAttempt>> = _attemptsFlow.asStateFlow()

    private val sampleReadingTests = listOf(
        ReadingTest(
            id = "reading_test_medium_01",
            title = "The Evolution of Offshore Renewable Wind Energy",
            difficultyLevel = "Medium",
            targetBand = 6.5,
            timeLimitMinutes = 60,
            passageText = """
                Paragraph A
                Over the past two decades, renewable energy technologies have transitioned from niche experimental concepts into mainstream drivers of global power generation. Among these, offshore wind energy has emerged as one of the most promising alternatives to fossil fuel dependence. Unlike onshore installations, which often face geographical constraints and local public opposition regarding visual noise, offshore wind farms harness the unobstructed, stronger, and more consistent winds available at sea.

                Paragraph B
                The engineering behind offshore wind turbines has advanced at a staggering pace. Modern turbines feature rotor diameters exceeding 200 meters and can generate up to 15 megawatts of electricity from a single unit. Crucially, innovations in floating foundation technology have expanded potential deployment areas beyond shallow coastal waters into deep oceanic zones. Floating platforms, anchored to the seabed with tension cables, allow energy producers to tap into deep-water winds that were previously inaccessible using fixed-bottom structures.

                Paragraph C
                Despite these technological breakthroughs, the environmental and economic impacts of offshore wind developments remain a subject of rigorous scientific study. Marine biologists emphasize that underwater noise during construction can disrupt marine mammal communication and navigation. However, post-construction ecological studies suggest that subsea turbine foundations often act as artificial reefs, fostering marine biodiversity and offering safe havens for aquatic species by restricting commercial trawling near wind farm perimeters.

                Paragraph D
                From an economic perspective, the levelized cost of offshore wind energy has fallen dramatically due to economies of scale and standardized manufacturing. Coastal economies in Northern Europe and East Asia are experiencing industrial revitalizations, with ports re-purposing shipbuilding yards to manufacture turbine components. As energy storage technologies mature, offshore wind is positioned to provide steady base-load power, accelerating the global transition toward zero-carbon energy grids.
            """.trimIndent(),
            questions = listOf(
                ReadingQuestion(
                    id = 1,
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "According to Paragraph A, what is a primary advantage of offshore wind farms over onshore installations?",
                    options = listOf(
                        "A) Lower manufacturing costs of turbine blades",
                        "B) Access to stronger, unobstructed, and more consistent sea winds",
                        "C) Complete immunity to coastal storm damage",
                        "D) Faster installation time in deep waters"
                    ),
                    correctAnswer = "B",
                    explanation = "Paragraph A explicitly states that offshore wind farms harness unobstructed, stronger, and more consistent winds available at sea compared to onshore sites."
                ),
                ReadingQuestion(
                    id = 2,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Floating foundation technology enables turbine installation in deep oceanic waters.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "TRUE",
                    explanation = "Paragraph B notes that floating platforms allow energy producers to tap into deep-water winds previously inaccessible with fixed-bottom structures."
                ),
                ReadingQuestion(
                    id = 3,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Underwater construction noise has caused permanent hearing loss in local dolphin populations.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "NOT GIVEN",
                    explanation = "Paragraph C mentions noise disruption to communication and navigation, but does NOT mention permanent hearing loss."
                ),
                ReadingQuestion(
                    id = 4,
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "Subsea foundations can act as artificial ______ that foster marine biodiversity.",
                    correctAnswer = "reefs",
                    explanation = "Paragraph C states that subsea turbine foundations often act as artificial reefs."
                ),
                ReadingQuestion(
                    id = 5,
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "Manufacturing turbine components has helped revitalize coastal economies in Northern ______ and East Asia.",
                    correctAnswer = "Europe",
                    explanation = "Paragraph D specifically mentions Northern Europe and East Asia."
                )
            )
        ),
        ReadingTest(
            id = "reading_test_easy_02",
            title = "Urban Agriculture and Controlled Environment Farming",
            difficultyLevel = "Easy",
            targetBand = 5.5,
            timeLimitMinutes = 60,
            passageText = """
                Paragraph A
                As global urban populations continue to expand rapidly, agricultural scientists are exploring innovative ways to grow fresh produce close to city centers. Urban agriculture—specifically vertical farming and hydroponic systems—offers a sustainable solution to traditional farming challenges, such as land scarcity, weather unpredictability, and long supply chain transportation emissions.

                Paragraph B
                Vertical farms stack crops in indoor, climate-controlled facilities using LED lighting tailored to optimize photosynthesis. Instead of soil, plants are suspended in nutrient-rich water solutions (hydroponics) or aerated mist (aeroponics). This closed-loop environment recirculates water, consuming up to 95% less water than traditional open-field agriculture while eliminating the need for synthetic chemical pesticides.

                Paragraph C
                While the environmental benefits are compelling, high initial capital expenditure for indoor automation, sensors, and electricity usage remains a barrier to widespread commercial adoption. Nevertheless, urban farms provide city dwellers with fresh, pesticide-free greens harvested daily, building local food resilience against climate disruptions.
            """.trimIndent(),
            questions = listOf(
                ReadingQuestion(
                    id = 1,
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "Which method is used in vertical farming instead of traditional soil?",
                    options = listOf(
                        "A) Synthetic chemical sprays",
                        "B) Nutrient-rich water solutions or aerated mist",
                        "C) Volcanic ash beds",
                        "D) Recycled paper pulp"
                    ),
                    correctAnswer = "B",
                    explanation = "Paragraph B describes hydroponic nutrient-rich water solutions and aeroponic mist as soil replacements."
                ),
                ReadingQuestion(
                    id = 2,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Vertical farming facilities can consume up to 95% less water than traditional field farming.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "TRUE",
                    explanation = "Paragraph B directly states closed-loop systems consume up to 95% less water."
                ),
                ReadingQuestion(
                    id = 3,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Government subsidies cover 80% of electricity costs for indoor urban farms.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "NOT GIVEN",
                    explanation = "The text mentions electricity costs as a barrier, but does NOT mention government subsidies."
                ),
                ReadingQuestion(
                    id = 4,
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "Vertical farms rely on climate-controlled facilities powered by LED ______.",
                    correctAnswer = "lighting",
                    explanation = "Paragraph B notes vertical farms use LED lighting tailored to optimize photosynthesis."
                )
            )
        ),
        ReadingTest(
            id = "reading_test_hard_03",
            title = "Neuroplasticity and Language Acquisition in Multilingual Adults",
            difficultyLevel = "Hard",
            targetBand = 7.5,
            timeLimitMinutes = 60,
            passageText = """
                Paragraph A
                For decades, neuroscientists posited that second-language acquisition was bound by a strict critical period ending around puberty, after which native-like proficiency became virtually unobtainable. Recent neuroimaging advancements, however, have challenged this rigid critical period hypothesis. Functional Magnetic Resonance Imaging (fMRI) reveals that adult brains exhibit remarkable neuroplasticity, dynamically restructuring cortical neural networks during intensive language immersion.

                Paragraph B
                In adult multilinguals, the prefrontal cortex and left inferior parietal lobule undergo structural gray matter volumetric increases. The cognitive demands of managing two or more active linguistic systems recruit the executive control network, sharpening working memory, task-switching agility, and attentional control. This phenomenon, often referred to as the 'bilingual advantage', demonstrates that linguistic exercise strengthens general cognitive control mechanisms.

                Paragraph C
                Furthermore, longitudinal epidemiological studies indicate that lifelong bilingualism builds cognitive reserve—a neuroprotective buffer against age-related cognitive decline. When pathology associated with neurodegenerative disorders such as Alzheimer's develops, bilingual individuals frequently compensate better than monolinguals, delaying the clinical onset of dementia symptoms by four to five years.
            """.trimIndent(),
            questions = listOf(
                ReadingQuestion(
                    id = 1,
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "What has modern fMRI neuroimaging revealed regarding adult language acquisition?",
                    options = listOf(
                        "A) Adult brains lose all neural adaptability after puberty",
                        "B) Adult brains exhibit neuroplasticity by restructuring neural networks during intensive language study",
                        "C) Language learning in adulthood shrinks gray matter density",
                        "D) Monolinguals possess stronger executive control networks"
                    ),
                    correctAnswer = "B",
                    explanation = "Paragraph A notes fMRI shows adult brains exhibit neuroplasticity and dynamically restructure cortical networks."
                ),
                ReadingQuestion(
                    id = 2,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Lifelong bilingualism has been shown to delay clinical dementia symptoms by 4 to 5 years.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "TRUE",
                    explanation = "Paragraph C states bilingualism builds cognitive reserve, delaying clinical onset of dementia symptoms by 4 to 5 years."
                ),
                ReadingQuestion(
                    id = 3,
                    type = QuestionType.TRUE_FALSE_NOT_GIVEN,
                    questionText = "Learning a third language in adulthood requires twice as much brain oxygen consumption as learning a second language.",
                    options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                    correctAnswer = "NOT GIVEN",
                    explanation = "The passage does NOT mention brain oxygen consumption ratios for third language learning."
                ),
                ReadingQuestion(
                    id = 4,
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "Lifelong bilingualism builds cognitive ______ which serves as a buffer against cognitive decline.",
                    correctAnswer = "reserve",
                    explanation = "Paragraph C specifically uses the phrase cognitive reserve."
                )
            )
        )
    )

    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        val userId = auth?.currentUser?.uid ?: profile.phone.ifEmpty { "default_user" }
        try {
            firestore?.collection("users")?.document(userId)?.set(profile)?.await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed saving user profile to Firestore: ${e.message}")
        }
    }

    suspend fun fetchReadingTests(): List<ReadingTest> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleReadingTests

        try {
            val snapshot = db.collection("readingTests").get().await()
            if (snapshot.isEmpty) {
                // Seed Firestore with sample tests
                seedReadingTests()
                return@withContext sampleReadingTests
            }

            val tests = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id
                    val title = doc.getString("title") ?: ""
                    val difficultyLevel = doc.getString("difficultyLevel") ?: "Medium"
                    val targetBand = doc.getDouble("targetBand") ?: 6.5
                    val timeLimitMinutes = (doc.getLong("timeLimitMinutes") ?: 60).toInt()
                    val passageText = doc.getString("passageText") ?: ""

                    @Suppress("UNCHECKED_CAST")
                    val rawQuestions = doc.get("questions") as? List<Map<String, Any>> ?: emptyList()

                    val questions = rawQuestions.map { qMap ->
                        val typeStr = qMap["type"] as? String ?: "MULTIPLE_CHOICE"
                        val qType = try { QuestionType.valueOf(typeStr) } catch (e: Exception) { QuestionType.MULTIPLE_CHOICE }
                        @Suppress("UNCHECKED_CAST")
                        val options = qMap["options"] as? List<String> ?: emptyList()

                        ReadingQuestion(
                            id = (qMap["id"] as? Long)?.toInt() ?: 0,
                            type = qType,
                            questionText = qMap["questionText"] as? String ?: "",
                            options = options,
                            correctAnswer = qMap["correctAnswer"] as? String ?: "",
                            explanation = qMap["explanation"] as? String ?: ""
                        )
                    }

                    ReadingTest(
                        id = id,
                        title = title,
                        difficultyLevel = difficultyLevel,
                        targetBand = targetBand,
                        timeLimitMinutes = timeLimitMinutes,
                        passageText = passageText,
                        questions = questions
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (tests.isEmpty()) sampleReadingTests else tests
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching reading tests: ${e.message}")
            sampleReadingTests
        }
    }

    suspend fun seedReadingTests() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (test in sampleReadingTests) {
                val qMapList = test.questions.map { q ->
                    mapOf(
                        "id" to q.id,
                        "type" to q.type.name,
                        "questionText" to q.questionText,
                        "options" to q.options,
                        "correctAnswer" to q.correctAnswer,
                        "explanation" to q.explanation
                    )
                }

                val docData = mapOf(
                    "title" to test.title,
                    "difficultyLevel" to test.difficultyLevel,
                    "targetBand" to test.targetBand,
                    "timeLimitMinutes" to test.timeLimitMinutes,
                    "passageText" to test.passageText,
                    "questions" to qMapList
                )

                db.collection("readingTests").document(test.id).set(docData).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed reading tests: ${e.message}")
        }
    }

    suspend fun saveReadingAttempt(attempt: ReadingAttempt) = withContext(Dispatchers.IO) {
        localAttempts.add(0, attempt)
        _attemptsFlow.value = localAttempts.toList()

        val userId = attempt.userId.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val attemptData = mapOf(
                "id" to attempt.id,
                "testId" to attempt.testId,
                "testTitle" to attempt.testTitle,
                "userId" to userId,
                "userAnswers" to attempt.userAnswers,
                "score" to attempt.score,
                "totalQuestions" to attempt.totalQuestions,
                "bandScore" to attempt.bandScore,
                "timeTakenSeconds" to attempt.timeTakenSeconds,
                "timestamp" to attempt.timestamp
            )

            db.collection("users")
                .document(userId)
                .collection("readingAttempts")
                .document(attempt.id)
                .set(attemptData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving reading attempt to Firestore: ${e.message}")
        }
    }

    private val sampleListeningTests = listOf(
        ListeningTest(
            id = "listening_test_medium_01",
            title = "IELTS Listening Section 1 & 2: Student Accommodation & Campus Library",
            difficultyLevel = "Medium",
            targetBand = 6.5,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            audioDurationSeconds = 180,
            transferTimeSeconds = 600,
            questions = listOf(
                ListeningQuestion(
                    id = 1,
                    sectionNumber = 1,
                    type = ListeningQuestionType.FORM_COMPLETION,
                    questionText = "Complete Question 1 on Room Preference in the Housing Form.",
                    formContext = "Student Name: Sarah Jenkins\nTarget Move-in Date: 15th September\nRoom Preference: [1]\nContact Extension: [2]",
                    correctAnswer = "En-suite",
                    explanation = "The student requests an en-suite single room option."
                ),
                ListeningQuestion(
                    id = 2,
                    sectionNumber = 1,
                    type = ListeningQuestionType.FORM_COMPLETION,
                    questionText = "Complete Question 2 on Contact Extension.",
                    formContext = "Contact Extension: 07700 [2]",
                    correctAnswer = "900142",
                    explanation = "The housing officer specifies contact extension 900142."
                ),
                ListeningQuestion(
                    id = 3,
                    sectionNumber = 2,
                    type = ListeningQuestionType.MULTIPLE_CHOICE,
                    questionText = "What time does the campus central library open on weekend mornings?",
                    options = listOf("A) 08:00 AM", "B) 09:30 AM", "C) 10:00 AM", "D) 12:00 PM"),
                    correctAnswer = "B",
                    explanation = "The library orientation states weekend opening hours are 09:30 AM."
                ),
                ListeningQuestion(
                    id = 4,
                    sectionNumber = 2,
                    type = ListeningQuestionType.MATCHING,
                    questionText = "Match the Group Study Rooms (Item 1) to their floor location in the library.",
                    matchingOptions = listOf("A - Ground Floor", "B - 2nd Floor East Wing", "C - 3rd Floor Basement"),
                    correctAnswer = "B",
                    explanation = "Group Study Rooms are located on the 2nd Floor East Wing."
                ),
                ListeningQuestion(
                    id = 5,
                    sectionNumber = 2,
                    type = ListeningQuestionType.MATCHING,
                    questionText = "Match the Multimedia Lab (Item 2) to its floor location in the library.",
                    matchingOptions = listOf("A - Ground Floor", "B - 2nd Floor East Wing", "C - 3rd Floor Basement"),
                    correctAnswer = "A",
                    explanation = "The audio specifies the Multimedia Lab is on the Ground Floor."
                )
            )
        ),
        ListeningTest(
            id = "listening_test_easy_02",
            title = "IELTS Listening Section 1: Community Center Sports Registration",
            difficultyLevel = "Easy",
            targetBand = 5.5,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            audioDurationSeconds = 150,
            transferTimeSeconds = 600,
            questions = listOf(
                ListeningQuestion(
                    id = 1,
                    sectionNumber = 1,
                    type = ListeningQuestionType.FORM_COMPLETION,
                    questionText = "Complete Question 1 on Membership Tier.",
                    formContext = "Membership Tier: [1]\nMonthly Fee: $45\nSport: Badminton",
                    correctAnswer = "Gold Pass",
                    explanation = "The caller selects the Gold Pass membership level."
                ),
                ListeningQuestion(
                    id = 2,
                    sectionNumber = 1,
                    type = ListeningQuestionType.MULTIPLE_CHOICE,
                    questionText = "How often are beginner swimming classes held?",
                    options = listOf("A) Once a week", "B) Twice a week", "C) Every weekend"),
                    correctAnswer = "B",
                    explanation = "Classes are held twice a week on Tuesdays and Thursdays."
                ),
                ListeningQuestion(
                    id = 3,
                    sectionNumber = 1,
                    type = ListeningQuestionType.MATCHING,
                    questionText = "Match the Fitness Gym operating hours.",
                    matchingOptions = listOf("A - 06:00 to 22:00", "B - 08:00 to 20:00", "C - 24 Hours"),
                    correctAnswer = "A",
                    explanation = "The gym operates daily from 06:00 to 22:00."
                )
            )
        )
    )

    suspend fun fetchListeningTests(): List<ListeningTest> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleListeningTests

        try {
            val snapshot = db.collection("listeningTests").get().await()
            if (snapshot.isEmpty) {
                seedListeningTests()
                return@withContext sampleListeningTests
            }

            val tests = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id
                    val title = doc.getString("title") ?: ""
                    val difficultyLevel = doc.getString("difficultyLevel") ?: "Medium"
                    val targetBand = doc.getDouble("targetBand") ?: 6.5
                    val audioUrl = doc.getString("audioUrl") ?: ""
                    val audioDurationSeconds = (doc.getLong("audioDurationSeconds") ?: 180).toInt()
                    val transferTimeSeconds = (doc.getLong("transferTimeSeconds") ?: 600).toInt()

                    @Suppress("UNCHECKED_CAST")
                    val rawQuestions = doc.get("questions") as? List<Map<String, Any>> ?: emptyList()

                    val questions = rawQuestions.map { qMap ->
                        val typeStr = qMap["type"] as? String ?: "MULTIPLE_CHOICE"
                        val qType = try { ListeningQuestionType.valueOf(typeStr) } catch (e: Exception) { ListeningQuestionType.MULTIPLE_CHOICE }
                        @Suppress("UNCHECKED_CAST")
                        val options = qMap["options"] as? List<String> ?: emptyList()
                        @Suppress("UNCHECKED_CAST")
                        val matchingOptions = qMap["matchingOptions"] as? List<String> ?: emptyList()

                        ListeningQuestion(
                            id = (qMap["id"] as? Long)?.toInt() ?: 0,
                            sectionNumber = (qMap["sectionNumber"] as? Long)?.toInt() ?: 1,
                            type = qType,
                            questionText = qMap["questionText"] as? String ?: "",
                            formContext = qMap["formContext"] as? String ?: "",
                            options = options,
                            matchingOptions = matchingOptions,
                            correctAnswer = qMap["correctAnswer"] as? String ?: "",
                            explanation = qMap["explanation"] as? String ?: ""
                        )
                    }

                    ListeningTest(
                        id = id,
                        title = title,
                        difficultyLevel = difficultyLevel,
                        targetBand = targetBand,
                        audioUrl = audioUrl,
                        audioDurationSeconds = audioDurationSeconds,
                        transferTimeSeconds = transferTimeSeconds,
                        questions = questions
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (tests.isEmpty()) sampleListeningTests else tests
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching listening tests: ${e.message}")
            sampleListeningTests
        }
    }

    suspend fun seedListeningTests() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (test in sampleListeningTests) {
                val qMapList = test.questions.map { q ->
                    mapOf(
                        "id" to q.id,
                        "sectionNumber" to q.sectionNumber,
                        "type" to q.type.name,
                        "questionText" to q.questionText,
                        "formContext" to q.formContext,
                        "options" to q.options,
                        "matchingOptions" to q.matchingOptions,
                        "correctAnswer" to q.correctAnswer,
                        "explanation" to q.explanation
                    )
                }

                val docData = mapOf(
                    "title" to test.title,
                    "difficultyLevel" to test.difficultyLevel,
                    "targetBand" to test.targetBand,
                    "audioUrl" to test.audioUrl,
                    "audioDurationSeconds" to test.audioDurationSeconds,
                    "transferTimeSeconds" to test.transferTimeSeconds,
                    "questions" to qMapList
                )

                db.collection("listeningTests").document(test.id).set(docData).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed listening tests: ${e.message}")
        }
    }

    suspend fun saveListeningAttempt(attempt: ListeningAttempt) = withContext(Dispatchers.IO) {
        val userId = attempt.userId.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val attemptData = mapOf(
                "id" to attempt.id,
                "testId" to attempt.testId,
                "testTitle" to attempt.testTitle,
                "userId" to userId,
                "userAnswers" to attempt.userAnswers,
                "score" to attempt.score,
                "totalQuestions" to attempt.totalQuestions,
                "bandScore" to attempt.bandScore,
                "timeTakenSeconds" to attempt.timeTakenSeconds,
                "timestamp" to attempt.timestamp
            )

            db.collection("users")
                .document(userId)
                .collection("listeningAttempts")
                .document(attempt.id)
                .set(attemptData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving listening attempt to Firestore: ${e.message}")
        }
    }

    private val sampleWritingTasks = listOf(
        WritingTask(
            id = "writing_task_01",
            taskType = "Task 1",
            title = "Global Renewable Energy Production Trends (2010–2025)",
            prompt = "The line chart illustrates the percentage of electricity generated from renewable sources in Europe, Asia-Pacific, North America, and Latin America from 2010 to 2025. Summarise the information by selecting and reporting the main features, and make comparisons where relevant.",
            imageUrl = "https://images.unsplash.com/photo-1466611653911-95081537e5b7?auto=format&fit=crop&w=800&q=80",
            difficultyLevel = "Medium",
            targetWordCount = 150,
            recommendedTimeMinutes = 20
        ),
        WritingTask(
            id = "writing_task_02",
            taskType = "Task 2",
            title = "Artificial Intelligence & Workplace Automation",
            prompt = "Some people believe that the increasing reliance on artificial intelligence and automation in the workplace will lead to widespread unemployment, while others argue it will create new opportunities and higher quality jobs. Discuss both views and give your own opinion.",
            imageUrl = null,
            difficultyLevel = "Medium",
            targetWordCount = 250,
            recommendedTimeMinutes = 40
        ),
        WritingTask(
            id = "writing_task_03",
            taskType = "Task 2",
            title = "Environmental Protection vs Economic Growth",
            prompt = "Economic development often results in environmental degradation. Some people think that governments should prioritize environmental protection over economic growth, while others believe that economic prosperity is essential for solving environmental issues. Discuss both sides and give your opinion.",
            imageUrl = null,
            difficultyLevel = "Hard",
            targetWordCount = 250,
            recommendedTimeMinutes = 40
        )
    )

    suspend fun fetchWritingTasks(): List<WritingTask> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleWritingTasks

        try {
            val snapshot = db.collection("writingTasks").get().await()
            if (snapshot.isEmpty) {
                seedWritingTasks()
                return@withContext sampleWritingTasks
            }

            val tasks = snapshot.documents.mapNotNull { doc ->
                try {
                    WritingTask(
                        id = doc.id,
                        taskType = doc.getString("taskType") ?: "Task 1",
                        title = doc.getString("title") ?: "",
                        prompt = doc.getString("prompt") ?: "",
                        imageUrl = doc.getString("imageUrl"),
                        difficultyLevel = doc.getString("difficultyLevel") ?: "Medium",
                        targetWordCount = (doc.getLong("targetWordCount") ?: 150).toInt(),
                        recommendedTimeMinutes = (doc.getLong("recommendedTimeMinutes") ?: 20).toInt()
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (tasks.isEmpty()) sampleWritingTasks else tasks
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching writing tasks: ${e.message}")
            sampleWritingTasks
        }
    }

    suspend fun seedWritingTasks() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (task in sampleWritingTasks) {
                val docData = mapOf(
                    "taskType" to task.taskType,
                    "title" to task.title,
                    "prompt" to task.prompt,
                    "imageUrl" to task.imageUrl,
                    "difficultyLevel" to task.difficultyLevel,
                    "targetWordCount" to task.targetWordCount,
                    "recommendedTimeMinutes" to task.recommendedTimeMinutes
                )
                db.collection("writingTasks").document(task.id).set(docData).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed writing tasks: ${e.message}")
        }
    }

    suspend fun saveWritingAttempt(attempt: WritingAttempt) = withContext(Dispatchers.IO) {
        val userId = attempt.userId.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val criteriaMapList = attempt.criteriaScores.map { c ->
                mapOf(
                    "criterionName" to c.criterionName,
                    "score" to c.score,
                    "feedbackNote" to c.feedbackNote
                )
            }

            val attemptData = mapOf(
                "id" to attempt.id,
                "taskId" to attempt.taskId,
                "taskTitle" to attempt.taskTitle,
                "taskType" to attempt.taskType,
                "userId" to userId,
                "inputMethod" to attempt.inputMethod,
                "answerText" to attempt.answerText,
                "photoUri" to attempt.photoUri,
                "overallBand" to attempt.overallBand,
                "criteriaScores" to criteriaMapList,
                "generalFeedback" to attempt.generalFeedback,
                "wordCount" to attempt.wordCount,
                "timeTakenSeconds" to attempt.timeTakenSeconds,
                "timestamp" to attempt.timestamp
            )

            db.collection("users")
                .document(userId)
                .collection("writingAttempts")
                .document(attempt.id)
                .set(attemptData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving writing attempt to Firestore: ${e.message}")
        }
    }

    private val sampleSpeakingTasks = listOf(
        SpeakingTask(
            id = "speaking_task_01",
            partNumber = 1,
            title = "Part 1: Hometown, Studies & Free Time",
            topic = "Personal & Daily Life Routine",
            part1Questions = listOf(
                "Let's talk about your hometown. Where is your hometown located?",
                "Do you work or are you a student? What do you find most interesting about it?",
                "What do you usually do in your free time to relax after a long day?"
            ),
            difficultyLevel = "Easy"
        ),
        SpeakingTask(
            id = "speaking_task_02",
            partNumber = 2,
            title = "Part 2: Cue Card - Memorable Journey",
            topic = "Travel & Memorable Experiences",
            part2CueCard = "Describe a memorable trip or journey you took that made a lasting impression on you.",
            part2Bullets = listOf(
                "Where you went and who you traveled with",
                "What activities you did during the trip",
                "Why this journey was particularly memorable to you"
            ),
            part3Questions = listOf(
                "How have people's travel habits changed in your country over the last decade?",
                "Do you think international tourism does more harm or good to local cultures?"
            ),
            difficultyLevel = "Medium"
        ),
        SpeakingTask(
            id = "speaking_task_03",
            partNumber = 3,
            title = "Part 3: Deep Discussion - AI & Future Society",
            topic = "Technology, Automation & Future Employment",
            part3Questions = listOf(
                "In what ways might artificial intelligence impact traditional employment structures in the next 20 years?",
                "Do you agree that ethical guidelines should regulate AI development globally?",
                "How can educational institutions adapt curricula to prepare youth for an automated workplace?"
            ),
            difficultyLevel = "Hard"
        )
    )

    suspend fun fetchSpeakingTasks(): List<SpeakingTask> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleSpeakingTasks

        try {
            val snapshot = db.collection("speakingTasks").get().await()
            if (snapshot.isEmpty) {
                seedSpeakingTasks()
                return@withContext sampleSpeakingTasks
            }

            val tasks = snapshot.documents.mapNotNull { doc ->
                try {
                    val p1 = (doc.get("part1Questions") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                    val p2b = (doc.get("part2Bullets") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                    val p3 = (doc.get("part3Questions") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()

                    SpeakingTask(
                        id = doc.id,
                        partNumber = (doc.getLong("partNumber") ?: 1).toInt(),
                        title = doc.getString("title") ?: "",
                        topic = doc.getString("topic") ?: "",
                        part1Questions = p1,
                        part2CueCard = doc.getString("part2CueCard") ?: "",
                        part2Bullets = p2b,
                        part3Questions = p3,
                        difficultyLevel = doc.getString("difficultyLevel") ?: "Medium"
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (tasks.isEmpty()) sampleSpeakingTasks else tasks
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching speaking tasks: ${e.message}")
            sampleSpeakingTasks
        }
    }

    suspend fun seedSpeakingTasks() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (task in sampleSpeakingTasks) {
                val docData = mapOf(
                    "partNumber" to task.partNumber,
                    "title" to task.title,
                    "topic" to task.topic,
                    "part1Questions" to task.part1Questions,
                    "part2CueCard" to task.part2CueCard,
                    "part2Bullets" to task.part2Bullets,
                    "part3Questions" to task.part3Questions,
                    "difficultyLevel" to task.difficultyLevel
                )
                db.collection("speakingTasks").document(task.id).set(docData).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed speaking tasks: ${e.message}")
        }
    }

    suspend fun saveSpeakingAttempt(attempt: SpeakingAttempt) = withContext(Dispatchers.IO) {
        val userId = attempt.userId.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val criteriaMapList = attempt.criteriaScores.map { c ->
                mapOf(
                    "criterionName" to c.criterionName,
                    "score" to c.score,
                    "feedbackNote" to c.feedbackNote
                )
            }

            val attemptData = mapOf(
                "id" to attempt.id,
                "taskId" to attempt.taskId,
                "taskTitle" to attempt.taskTitle,
                "partNumber" to attempt.partNumber,
                "userId" to userId,
                "audioFilePath" to attempt.audioFilePath,
                "transcription" to attempt.transcription,
                "overallBand" to attempt.overallBand,
                "criteriaScores" to criteriaMapList,
                "generalFeedback" to attempt.generalFeedback,
                "pronunciationAssessmentNote" to attempt.pronunciationAssessmentNote,
                "instructorReview" to attempt.instructorReview,
                "timestamp" to attempt.timestamp
            )

            db.collection("users")
                .document(userId)
                .collection("speakingAttempts")
                .document(attempt.id)
                .set(attemptData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving speaking attempt to Firestore: ${e.message}")
        }
    }

    private val sampleVideoLessons = listOf(
        VideoLesson(
            id = "lesson_l1",
            title = "Mastering Section 1 Form Completion",
            skillCategory = "Listening",
            topic = "Form & Table Completion",
            description = "Avoid the spelling and number mistakes students make most in Section 1",
            duration = "08:15",
            thumbnailUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        ),
        VideoLesson(
            id = "lesson_l2",
            title = "Map & Layout Labelling Walkthrough",
            skillCategory = "Listening",
            topic = "Map & Diagram Matching",
            description = "The direction words you need for map and diagram questions",
            duration = "10:30",
            thumbnailUrl = "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
        ),
        VideoLesson(
            id = "lesson_r1",
            title = "Skimming & Scanning Academic Passages",
            skillCategory = "Reading",
            topic = "Time Management & Key Words",
            description = "How to get through all 3 passages in 60 minutes",
            duration = "12:00",
            thumbnailUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
        ),
        VideoLesson(
            id = "lesson_r2",
            title = "True / False / Not Given Logic Demystified",
            skillCategory = "Reading",
            topic = "Factual & Claim Matching",
            description = "Disambiguate 'Not Given' claims from contradicted facts using text logic and keyword mapping techniques.",
            duration = "09:45",
            thumbnailUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoylines.mp4"
        ),
        VideoLesson(
            id = "lesson_w1",
            title = "Task 1 Overview & Trend Analysis Formulations",
            skillCategory = "Writing",
            topic = "Academic Task 1 Line & Bar Charts",
            description = "How to write a Band 8+ overall summary sentence and compare main data trends with high-scoring grammatical accuracy.",
            duration = "14:20",
            thumbnailUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
        ),
        VideoLesson(
            id = "lesson_s1",
            title = "Part 2 Cue Card 2-Minute Storytelling Flow",
            skillCategory = "Speaking",
            topic = "Cue Card Delivery & Coherence",
            description = "Step-by-step cue card preparation method to structure your 1-minute notes and maintain natural speech for 2 full minutes.",
            duration = "11:10",
            thumbnailUrl = "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?auto=format&fit=crop&w=600&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        )
    )

    suspend fun fetchVideoLessons(): List<VideoLesson> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleVideoLessons

        try {
            val snapshot = db.collection("videoLessons").get().await()
            if (snapshot.isEmpty) {
                seedVideoLessons()
                return@withContext sampleVideoLessons
            }

            val lessons = snapshot.documents.mapNotNull { doc ->
                try {
                    VideoLesson(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        skillCategory = doc.getString("skillCategory") ?: "General",
                        topic = doc.getString("topic") ?: "",
                        description = doc.getString("description") ?: "",
                        duration = doc.getString("duration") ?: "",
                        thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                        videoUrl = doc.getString("videoUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (lessons.isEmpty()) sampleVideoLessons else lessons
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching video lessons: ${e.message}")
            sampleVideoLessons
        }
    }

    suspend fun seedVideoLessons() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (lesson in sampleVideoLessons) {
                val docData = mapOf(
                    "title" to lesson.title,
                    "skillCategory" to lesson.skillCategory,
                    "topic" to lesson.topic,
                    "description" to lesson.description,
                    "duration" to lesson.duration,
                    "thumbnailUrl" to lesson.thumbnailUrl,
                    "videoUrl" to lesson.videoUrl
                )
                db.collection("videoLessons").document(lesson.id).set(docData).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed video lessons: ${e.message}")
        }
    }

    suspend fun fetchUserLessonProgress(userIdParam: String): Map<String, LessonProgress> = withContext(Dispatchers.IO) {
        val userId = userIdParam.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext emptyMap()

        try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("lessonProgress")
                .get()
                .await()

            val progressMap = mutableMapOf<String, LessonProgress>()
            for (doc in snapshot.documents) {
                val lessonId = doc.id
                val lastPositionMs = doc.getLong("lastPositionMs") ?: 0L
                val durationMs = doc.getLong("durationMs") ?: 0L
                val completed = doc.getBoolean("completed") ?: false
                val updated = doc.getLong("lastUpdatedTimestamp") ?: System.currentTimeMillis()

                progressMap[lessonId] = LessonProgress(
                    lessonId = lessonId,
                    lastPositionMs = lastPositionMs,
                    durationMs = durationMs,
                    completed = completed,
                    lastUpdatedTimestamp = updated
                )
            }
            progressMap
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching user lesson progress: ${e.message}")
            emptyMap()
        }
    }

    suspend fun saveUserLessonProgress(userIdParam: String, progress: LessonProgress) = withContext(Dispatchers.IO) {
        val userId = userIdParam.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val data = mapOf(
                "lessonId" to progress.lessonId,
                "lastPositionMs" to progress.lastPositionMs,
                "durationMs" to progress.durationMs,
                "completed" to progress.completed,
                "lastUpdatedTimestamp" to progress.lastUpdatedTimestamp
            )

            db.collection("users")
                .document(userId)
                .collection("lessonProgress")
                .document(progress.lessonId)
                .set(data)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving user lesson progress: ${e.message}")
        }
    }

    private val sampleMockExams = listOf(
        MockExam(
            id = "mock_weekly_01",
            title = "Weekly Mock Exam #1 (Standard)",
            examType = "WEEKLY",
            difficultyLevel = "Standard",
            startDate = "2026-07-28",
            endDate = "2026-08-04",
            listeningTestId = "listening_test_medium_01",
            readingTestId = "reading_test_medium_01",
            writingTaskId = "writing_task_02",
            speakingTaskId = "speaking_task_01",
            totalDurationMinutes = 160
        ),
        MockExam(
            id = "mock_monthly_01",
            title = "Monthly Full Sitting Exam (Challenging)",
            examType = "MONTHLY",
            difficultyLevel = "Challenging",
            startDate = "2026-07-01",
            endDate = "2026-07-31",
            listeningTestId = "listening_test_easy_02",
            readingTestId = "reading_test_hard_03",
            writingTaskId = "writing_task_03",
            speakingTaskId = "speaking_task_02",
            totalDurationMinutes = 160
        )
    )

    suspend fun fetchMockExams(): List<MockExam> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) return@withContext sampleMockExams

        try {
            val snapshot = db.collection("mockExams").get().await()
            if (snapshot.isEmpty) {
                seedMockExams()
                return@withContext sampleMockExams
            }

            val exams = snapshot.documents.mapNotNull { doc ->
                try {
                    MockExam(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        examType = doc.getString("examType") ?: "WEEKLY",
                        difficultyLevel = doc.getString("difficultyLevel") ?: "Standard",
                        startDate = doc.getString("startDate") ?: "",
                        endDate = doc.getString("endDate") ?: "",
                        listeningTestId = doc.getString("listeningTestId") ?: "listening_test_medium_01",
                        readingTestId = doc.getString("readingTestId") ?: "reading_test_medium_01",
                        writingTaskId = doc.getString("writingTaskId") ?: "writing_task_02",
                        speakingTaskId = doc.getString("speakingTaskId") ?: "speaking_task_01",
                        totalDurationMinutes = (doc.getLong("totalDurationMinutes") ?: 160).toInt()
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (exams.isEmpty()) sampleMockExams else exams
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching mock exams: ${e.message}")
            sampleMockExams
        }
    }

    suspend fun seedMockExams() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (exam in sampleMockExams) {
                val data = mapOf(
                    "title" to exam.title,
                    "examType" to exam.examType,
                    "difficultyLevel" to exam.difficultyLevel,
                    "startDate" to exam.startDate,
                    "endDate" to exam.endDate,
                    "listeningTestId" to exam.listeningTestId,
                    "readingTestId" to exam.readingTestId,
                    "writingTaskId" to exam.writingTaskId,
                    "speakingTaskId" to exam.speakingTaskId,
                    "totalDurationMinutes" to exam.totalDurationMinutes
                )
                db.collection("mockExams").document(exam.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed mock exams: ${e.message}")
        }
    }

    suspend fun fetchUserMockAttempts(userIdParam: String): List<MockExamAttempt> = withContext(Dispatchers.IO) {
        val userId = userIdParam.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext emptyList()

        try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("mockExamAttempts")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    MockExamAttempt(
                        attemptId = doc.id,
                        mockExamId = doc.getString("mockExamId") ?: "",
                        userId = doc.getString("userId") ?: userId,
                        examTitle = doc.getString("examTitle") ?: "",
                        examType = doc.getString("examType") ?: "WEEKLY",
                        startTimeMillis = doc.getLong("startTimeMillis") ?: 0L,
                        endTimeMillis = doc.getLong("endTimeMillis") ?: 0L,
                        status = doc.getString("status") ?: "COMPLETED",
                        currentSection = doc.getString("currentSection") ?: "COMPLETED",
                        listeningBand = doc.getDouble("listeningBand") ?: 0.0,
                        readingBand = doc.getDouble("readingBand") ?: 0.0,
                        writingBand = doc.getDouble("writingBand") ?: 0.0,
                        speakingBand = doc.getDouble("speakingBand") ?: 0.0,
                        overallBand = doc.getDouble("overallBand") ?: 0.0,
                        listeningAnswersJson = doc.getString("listeningAnswersJson") ?: "",
                        readingAnswersJson = doc.getString("readingAnswersJson") ?: "",
                        writingEssayText = doc.getString("writingEssayText") ?: "",
                        speakingAudioUri = doc.getString("speakingAudioUri") ?: "",
                        officialTimeUpTimeMillis = doc.getLong("officialTimeUpTimeMillis") ?: 0L,
                        actualSubmissionTimeMillis = doc.getLong("actualSubmissionTimeMillis") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.startTimeMillis }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching user mock attempts: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveMockExamAttempt(userIdParam: String, attempt: MockExamAttempt) = withContext(Dispatchers.IO) {
        val userId = userIdParam.ifEmpty { auth?.currentUser?.uid ?: "default_user" }
        val db = firestore ?: return@withContext

        try {
            val data = mapOf(
                "attemptId" to attempt.attemptId,
                "mockExamId" to attempt.mockExamId,
                "userId" to userId,
                "examTitle" to attempt.examTitle,
                "examType" to attempt.examType,
                "startTimeMillis" to attempt.startTimeMillis,
                "endTimeMillis" to attempt.endTimeMillis,
                "status" to attempt.status,
                "currentSection" to attempt.currentSection,
                "listeningBand" to attempt.listeningBand,
                "readingBand" to attempt.readingBand,
                "writingBand" to attempt.writingBand,
                "speakingBand" to attempt.speakingBand,
                "overallBand" to attempt.overallBand,
                "listeningAnswersJson" to attempt.listeningAnswersJson,
                "readingAnswersJson" to attempt.readingAnswersJson,
                "writingEssayText" to attempt.writingEssayText,
                "speakingAudioUri" to attempt.speakingAudioUri,
                "officialTimeUpTimeMillis" to attempt.officialTimeUpTimeMillis,
                "actualSubmissionTimeMillis" to attempt.actualSubmissionTimeMillis
            )

            db.collection("users")
                .document(userId)
                .collection("mockExamAttempts")
                .document(attempt.attemptId)
                .set(data)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving mock exam attempt: ${e.message}")
        }
    }

    // ==========================================
    // PARTNER CENTER LOCATOR & BOOKING MODULE
    // ==========================================

    private val samplePartnerCenters = listOf(
        PartnerCenter(
            id = "center_dhanmondi_01",
            name = "Dhanmondi Practice Academy & IELTS Lab",
            address = "4th Floor, House 12, Road 5, Dhanmondi, Dhaka",
            city = "Dhaka",
            latitude = 23.7461,
            longitude = 90.3742,
            description = "State-of-the-art practice facility with high-fidelity headphone stations for practice listening tests and dedicated exam desks.",
            contactPhone = "+880 1711-223344",
            contactEmail = "dhanmondi@partnerpractice.bd",
            rating = 4.9,
            slots = listOf(
                PartnerSlot(
                    slotId = "slot_dhaka_01",
                    date = "2026-08-05",
                    time = "10:00 AM - 01:00 PM",
                    title = "Full Practice Mock Test (4 Skills)",
                    slotType = "PRACTICE_MOCK",
                    priceBdt = 1500.0,
                    capacity = 20,
                    seatsRemaining = 8
                ),
                PartnerSlot(
                    slotId = "slot_dhaka_02",
                    date = "2026-08-05",
                    time = "03:00 PM - 05:00 PM",
                    title = "Writing Task 1 & 2 Feedback Session",
                    slotType = "COACHING_SESSION",
                    priceBdt = 800.0,
                    capacity = 15,
                    seatsRemaining = 12
                ),
                PartnerSlot(
                    slotId = "slot_dhaka_03",
                    date = "2026-08-08",
                    time = "10:00 AM - 01:00 PM",
                    title = "Weekly Full Practice Mock Sitting",
                    slotType = "PRACTICE_MOCK",
                    priceBdt = 1500.0,
                    capacity = 20,
                    seatsRemaining = 15
                )
            )
        ),
        PartnerCenter(
            id = "center_uttara_02",
            name = "Uttara IELTS Practice & Prep Center",
            address = "Level 5, Sector 3, Uttara Model Town, Dhaka",
            city = "Dhaka",
            latitude = 23.8759,
            longitude = 90.3795,
            description = "Quiet, sound-proof practice rooms and expert evaluation coaches for mock speaking interviews and writing review.",
            contactPhone = "+880 1812-334455",
            contactEmail = "uttara@partnerprep.bd",
            rating = 4.8,
            slots = listOf(
                PartnerSlot(
                    slotId = "slot_uttara_01",
                    date = "2026-08-06",
                    time = "09:30 AM - 12:30 PM",
                    title = "Full Practice Mock Test (4 Skills)",
                    slotType = "PRACTICE_MOCK",
                    priceBdt = 1400.0,
                    capacity = 25,
                    seatsRemaining = 10
                ),
                PartnerSlot(
                    slotId = "slot_uttara_02",
                    date = "2026-08-07",
                    time = "02:30 PM - 04:30 PM",
                    title = "1-on-1 Speaking Mock Interview",
                    slotType = "COACHING_SESSION",
                    priceBdt = 1000.0,
                    capacity = 10,
                    seatsRemaining = 5
                )
            )
        ),
        PartnerCenter(
            id = "center_agrabad_03",
            name = "Agrabad Practice Hub & Coaching",
            address = "Jahan Building, Agrabad Commercial Area, Chittagong",
            city = "Chittagong",
            latitude = 22.3244,
            longitude = 91.8143,
            description = "Premier Chittagong center offering timed full mock exam sittings with instant score analytics.",
            contactPhone = "+880 1913-445566",
            contactEmail = "ctg.agrabad@partnerprep.bd",
            rating = 4.7,
            slots = listOf(
                PartnerSlot(
                    slotId = "slot_ctg_01",
                    date = "2026-08-06",
                    time = "10:00 AM - 01:00 PM",
                    title = "Chittagong Full Practice Mock Sitting",
                    slotType = "PRACTICE_MOCK",
                    priceBdt = 1300.0,
                    capacity = 20,
                    seatsRemaining = 12
                ),
                PartnerSlot(
                    slotId = "slot_ctg_02",
                    date = "2026-08-09",
                    time = "04:00 PM - 06:00 PM",
                    title = "Listening & Reading Strategy Workshop",
                    slotType = "COACHING_SESSION",
                    priceBdt = 750.0,
                    capacity = 20,
                    seatsRemaining = 18
                )
            )
        ),
        PartnerCenter(
            id = "center_sylhet_04",
            name = "Sylhet Zindabazar Practice Center",
            address = "3rd Floor, Millennium Market, Zindabazar, Sylhet",
            city = "Sylhet",
            latitude = 24.8949,
            longitude = 91.8687,
            description = "Spacious study hall with real exam condition environment for practice mock tests.",
            contactPhone = "+880 1614-556677",
            contactEmail = "sylhet@partnerprep.bd",
            rating = 4.8,
            slots = listOf(
                PartnerSlot(
                    slotId = "slot_sylhet_01",
                    date = "2026-08-07",
                    time = "10:00 AM - 01:00 PM",
                    title = "Sylhet Full Practice Mock Test",
                    slotType = "PRACTICE_MOCK",
                    priceBdt = 1350.0,
                    capacity = 20,
                    seatsRemaining = 14
                )
            )
        )
    )

    suspend fun getPartnerCenters(): List<PartnerCenter> {
        val db = firestore ?: return samplePartnerCenters
        return try {
            val snapshot = db.collection("partnerCenters").get().await()
            if (snapshot.isEmpty) {
                seedPartnerCenters(db)
                samplePartnerCenters
            } else {
                snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val name = doc.getString("name") ?: ""
                        val address = doc.getString("address") ?: ""
                        val city = doc.getString("city") ?: ""
                        val latitude = doc.getDouble("latitude") ?: 0.0
                        val longitude = doc.getDouble("longitude") ?: 0.0
                        val description = doc.getString("description") ?: ""
                        val contactPhone = doc.getString("contactPhone") ?: ""
                        val contactEmail = doc.getString("contactEmail") ?: ""
                        val rating = doc.getDouble("rating") ?: 4.8

                        val slotsList = (doc.get("slots") as? List<Map<String, Any>>)?.map { slotMap ->
                            PartnerSlot(
                                slotId = slotMap["slotId"] as? String ?: "",
                                date = slotMap["date"] as? String ?: "",
                                time = slotMap["time"] as? String ?: "",
                                title = slotMap["title"] as? String ?: "",
                                slotType = slotMap["slotType"] as? String ?: "PRACTICE_MOCK",
                                priceBdt = (slotMap["priceBdt"] as? Number)?.toDouble() ?: 1500.0,
                                capacity = (slotMap["capacity"] as? Number)?.toInt() ?: 20,
                                seatsRemaining = (slotMap["seatsRemaining"] as? Number)?.toInt() ?: 20
                            )
                        } ?: emptyList()

                        PartnerCenter(
                            id = id,
                            name = name,
                            address = address,
                            city = city,
                            latitude = latitude,
                            longitude = longitude,
                            description = description,
                            contactPhone = contactPhone,
                            contactEmail = contactEmail,
                            rating = rating,
                            slots = slotsList
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching partner centers: ${e.message}")
            samplePartnerCenters
        }
    }

    private suspend fun seedPartnerCenters(db: FirebaseFirestore) {
        try {
            for (center in samplePartnerCenters) {
                val data = mapOf(
                    "name" to center.name,
                    "address" to center.address,
                    "city" to center.city,
                    "latitude" to center.latitude,
                    "longitude" to center.longitude,
                    "description" to center.description,
                    "contactPhone" to center.contactPhone,
                    "contactEmail" to center.contactEmail,
                    "rating" to center.rating,
                    "slots" to center.slots.map { slot ->
                        mapOf(
                            "slotId" to slot.slotId,
                            "date" to slot.date,
                            "time" to slot.time,
                            "title" to slot.title,
                            "slotType" to slot.slotType,
                            "priceBdt" to slot.priceBdt,
                            "capacity" to slot.capacity,
                            "seatsRemaining" to slot.seatsRemaining
                        )
                    }
                )
                db.collection("partnerCenters").document(center.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error seeding partner centers: ${e.message}")
        }
    }

    /**
     * Executes atomic Firestore transaction to book slot and decrement seatsRemaining.
     * Prevents race condition if two students book last seat simultaneously.
     */
    suspend fun bookCenterSlotAtomic(booking: CenterBooking): Result<CenterBooking> {
        val db = firestore
            ?: return Result.failure(Exception("Database unavailable. Please check internet connection."))

        return try {
            val centerRef = db.collection("partnerCenters").document(booking.centerId)

            val updatedBooking = db.runTransaction { transaction ->
                val snapshot = transaction.get(centerRef)
                if (!snapshot.exists()) {
                    throw Exception("Selected center does not exist.")
                }

                val slotsListRaw = snapshot.get("slots") as? List<Map<String, Any>>
                    ?: throw Exception("No slots found for center.")

                var seatFoundAndAvailable = false
                val updatedSlots = slotsListRaw.map { slotMap ->
                    val sId = slotMap["slotId"] as? String ?: ""
                    if (sId == booking.slotId) {
                        val currentSeats = (slotMap["seatsRemaining"] as? Number)?.toInt() ?: 0
                        if (currentSeats <= 0) {
                            throw Exception("Sorry, this practice slot is fully booked! No seats remaining.")
                        }
                        seatFoundAndAvailable = true
                        slotMap.toMutableMap().apply {
                            this["seatsRemaining"] = currentSeats - 1
                        }
                    } else {
                        slotMap
                    }
                }

                if (!seatFoundAndAvailable) {
                    throw Exception("Practice slot not found.")
                }

                // 1. Update center slots with decremented seat count
                transaction.update(centerRef, "slots", updatedSlots)

                // 2. Save booking in user profile collection
                val userBookingRef = db.collection("users")
                    .document(booking.userId)
                    .collection("centerBookings")
                    .document(booking.bookingId)

                val bookingData = mapOf(
                    "bookingId" to booking.bookingId,
                    "userId" to booking.userId,
                    "userName" to booking.userName,
                    "userPhone" to booking.userPhone,
                    "centerId" to booking.centerId,
                    "centerName" to booking.centerName,
                    "centerAddress" to booking.centerAddress,
                    "slotId" to booking.slotId,
                    "slotDate" to booking.slotDate,
                    "slotTime" to booking.slotTime,
                    "slotTitle" to booking.slotTitle,
                    "slotType" to booking.slotType,
                    "priceBdt" to booking.priceBdt,
                    "paymentStatus" to booking.paymentStatus,
                    "paymentTranId" to booking.paymentTranId,
                    "bookingStatus" to booking.bookingStatus,
                    "createdAtMillis" to booking.createdAtMillis
                )

                transaction.set(userBookingRef, bookingData)

                // 3. Save copy in partner center's bookings collection
                val centerBookingRef = centerRef.collection("bookings").document(booking.bookingId)
                transaction.set(centerBookingRef, bookingData)

                booking
            }.await()

            Result.success(updatedBooking)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Atomic booking transaction failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserCenterBookings(): List<CenterBooking> {
        val userId = auth?.currentUser?.uid ?: "local_user"
        val db = firestore ?: return emptyList()

        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("centerBookings")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    CenterBooking(
                        bookingId = doc.getString("bookingId") ?: doc.id,
                        userId = doc.getString("userId") ?: "",
                        userName = doc.getString("userName") ?: "",
                        userPhone = doc.getString("userPhone") ?: "",
                        centerId = doc.getString("centerId") ?: "",
                        centerName = doc.getString("centerName") ?: "",
                        centerAddress = doc.getString("centerAddress") ?: "",
                        slotId = doc.getString("slotId") ?: "",
                        slotDate = doc.getString("slotDate") ?: "",
                        slotTime = doc.getString("slotTime") ?: "",
                        slotTitle = doc.getString("slotTitle") ?: "",
                        slotType = doc.getString("slotType") ?: "",
                        priceBdt = doc.getDouble("priceBdt") ?: 0.0,
                        paymentStatus = doc.getString("paymentStatus") ?: "SUCCESS",
                        paymentTranId = doc.getString("paymentTranId") ?: "",
                        bookingStatus = doc.getString("bookingStatus") ?: "CONFIRMED",
                        createdAtMillis = doc.getLong("createdAtMillis") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching user center bookings: ${e.message}")
            emptyList()
        }
    }
}


