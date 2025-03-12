import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

sealed class Scales {
    data class MapScales(val scales: Map<String, Scale>? = null) : Scales()
    data class SingleScale(val answers: Map<String,Map<String, Map<String, AnswerScale>>>? = null) : Scales()
}

data class Counting(
    val scales: Scales? = null
)

data class TestData(
    val meta: MetaData? = null,
    val questions: Map<String, Question>? = null,
    val counting: Counting? = null
)

data class Question(
    val question: String? = null,
    val dsc: String? = null,
    val type: String? = null,
    val answers: Map<String, Answer>? = null
)

data class MetaData(
    val machine_name: String? = null,
    val name: String? = null,
    val prefix_name: String? = null,
    val logo: String? = null,
    val authors: String? = null,
    val short_dsc: String? = null,
    val dsc: String? = null,
    val instruction: String? = null,
    val age: String? = null,
    val time: String? = null,
    val questionsCount: String? = null
)

data class Answer(
    val answer: String? = null
)

data class Scale(
    val answers: Map<String, AnswerScale>? = null // Key is question number (String)
)

data class AnswerScale(
    val points: String? = null,
    val answer_num: String? = null
)

class ScalesDeserializer : JsonDeserializer<Scales> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Scales {
        return if (json.isJsonObject && json.asJsonObject.has("answers")) {
            val answersMapType = object : TypeToken<Map<String,Map<String, Map<String, AnswerScale>>>>() {}.type
            val answersMap: Map<String,Map<String, Map<String, AnswerScale>>> = context.deserialize(json, answersMapType)
            Scales.SingleScale(answersMap)
        } else {
            val scalesMapType = object : TypeToken<Map<String, Scale>>() {}.type
            val scalesMap: Map<String, Scale> = context.deserialize(json, scalesMapType)
            Scales.MapScales(scalesMap)
        }
    }
}

fun decodeTestData(json: String): TestData? {
    val gson = GsonBuilder()
        .registerTypeAdapter(Scales::class.java, ScalesDeserializer())
        .create()

    return try {
        gson.fromJson(json, TestData::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun main() {
    val assessmentJson = """
{
    "meta": {
        "machine_name": "assessment",
        "name": "Как я оцениваю события и переживания",
        "logo": "system/db/images/tests/methodics/motivation/assessment2/logo.jpg",
        "authors": "",
        "short_dsc": "Тебе предстоит оценить события, случаи, переживания и решить можешь ли ты отнести его к себе.",
        "dsc": "",
        "instruction": "",
        "age": "7",
        "time": "15",
        "questions": "53"
    },
    "questions": {
        "1": {
            "question": "Тебе трудно думать о чем-нибудь одном.",
            "dsc": "В предложениях описаны события, случаи, переживания. Внимательно прочти каждое предложение и реши, можешь ли ты отнести его к себе, правильно ли оно описывает тебя, твое поведение, качества? Если да, нажми «Верно», если нет нажми «Неверно». Не думай над ответом долго. Если не можешь решить, верно или неверно то, о чем говорится в предложении, выбирай то, что бывает, как тебе кажется, чаще.",
            "type": "buttons",
            "answers": {
                "1": {"answer": "Верно"},
                "2": {"answer": "Неверно"}
            }
        },
        "2": {
            "question": "Тевает тебя, твое поведение, качества? Если да, нажми «Верно», если нет нажми «Неверно». Не думай над ответом долго. Если не можешь решить, верно или неверно то, о чем говорится в предложении, выбирай то, что бывает, как тебе кажется, чаще.",
            "type": "buttons",
            "answers": {
                "1": {"answer": "Верно"},
                "2": {"answer": "Неверно"}
            }
        }
    },
    "counting": {
        "scales": {
            "Социальная желательность": {
                "answers": {
                    "5": {"points": 1, "answer_num": 1},
                    "17": {"points": 1, "answer_num": 1},
                    "21": {"points": 1, "answer_num": 1},
                    "30": {"points": 1, "answer_num": 1},
                    "34": {"points": 1, "answer_num": 1},
                    "36": {"points": 1, "answer_num": 1},
                    "10": {"points": 1, "answer_num": 2},
                    "41": {"points": 1, "answer_num": 2},
                    "47": {"points": 1, "answer_num": 2},
                    "49": {"points": 1, "answer_num": 2},
                    "52": {"points": 1, "answer_num": 2}
                }
            },
            "Тревожность": {
                "answers": {
                    "1": {"points": 1, "answer_num": 1},
                    "2": {"points": 1, "answer_num": 1}
                }
            }
        }
    }
}
"""

    val edumotivationJson = """
{
    "meta": {
        "machine_name": "edumotivation",
        "name": "Учебная мотивация",
        "prefix_name": "Анкета",
        "logo": "system/db/images/tests/methodics/motivation/eduMotivation1/logo.jpg",
        "authors": "",
        "short_dsc": "Дорогой друг! Тебе будут заданы вопросы, на которые нужно дать честный ответ.",
        "dsc": "",
        "instruction": "",
        "age": "7",
        "time": "5",
        "questions": "10"
    },
    "questions": {
        "1": {
            "question": "Тебе нравится в школе?",
            "dsc": "Прочитай внимательно вопрос. Подумай, нравиться или хотелось бы тебе делать то, о чем тебя спрашивают? Выбери один из предложенных ответов. Чем честнее ты ответишь на вопрос, тем точнее будет результат.",
            "type": "radios",
            "answers": {
                "1": {
                    "answer": "Не очень"
                },
                "2": {
                    "answer": "Нравиться"
                },
                "3": {
                    "answer": "Не нравиться"
                }
            }
        },
        "2": {
            "question": "Утром, когда ты просыпаешься, ты всегда с радостью идешь в школу или тебе часто хочется остаться дома?",
            "dsc": "Прочитай внимательно вопрос. Подумай, нравиться или хотелось бы тебе делать то, о чем тебя спрашивают? Выбери один из предложенных ответов. Чем честнее ты ответишь на вопрос, тем точнее будет результат.",
            "type": "radios",
            "answers": {
                "1": {
                    "answer": "Чаще хочется остаться дома"
                },
                "2": {
                    "answer": "Бывает по-разному"
                },
                "3": {
                    "answer": "Иду с радостью"
                }
            }
        }
    },
    "counting": {
        "scales": {
            "answers": {
                "1": {"1": {"points": 1, "answer_num": 1}, "2": {"points": 3, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}},
                "2": {"1": {"points": 0, "answer_num": 1}, "2": {"points": 1, "answer_num": 2}, "3": {"points": 3, "answer_num": 3}},
                "3": {"1": {"points": 1, "answer_num": 1}, "2": {"points": 0, "answer_num": 2}, "3": {"points": 3, "answer_num": 3}},
                "4": {"1": {"points": 3, "answer_num": 1}, "2": {"points": 1, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}},
                "5": {"1": {"points": 0, "answer_num": 1}, "2": {"points": 3, "answer_num": 2}, "3": {"points": 1, "answer_num": 3}},
                "6": {"1": {"points": 1, "answer_num": 1}, "2": {"points": 3, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}},
                "7": {"1": {"points": 3, "answer_num": 1}, "2": {"points": 1, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}},
                "8": {"1": {"points": 1, "answer_num": 1}, "2": {"points": 0, "answer_num": 2}, "3": {"points": 3, "answer_num": 3}},
                "9": {"1": {"points": 1, "answer_num": 1}, "2": {"points": 3, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}},
                "10": {"1": {"points": 3, "answer_num": 1}, "2": {"points": 1, "answer_num": 2}, "3": {"points": 0, "answer_num": 3}}
            }
        }
    }
}
"""

    val assessmentData = decodeTestData(assessmentJson)
    println("Assessment Data: ${assessmentData?.counting}")

    val edumotivationData = decodeTestData(edumotivationJson)
    println("Edumotivation Data: ${edumotivationData?.counting}")
}