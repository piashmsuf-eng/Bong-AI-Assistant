package com.bong.assistant

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object SkillManager {
    private const val TAG = "SkillManager"
    private val skills = mutableListOf<Skill>()

    data class Skill(
        val id: String,
        val keywords: List<String>,
        val action: String,
        val packageName: String
    )

    fun loadSkills(context: Context) {
        try {
            val inputStream = context.assets.open("skills.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val action = obj.getString("action")
                val pkg = obj.optString("package", "")
                
                val keywordsJson = obj.getJSONArray("keywords")
                val keywords = mutableListOf<String>()
                for (j in 0 until keywordsJson.length()) {
                    keywords.add(keywordsJson.getString(j))
                }

                skills.add(Skill(id, keywords, action, pkg))
            }
            Log.d(TAG, "Loaded ${skills.size} skills")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading skills", e)
        }
    }

    fun findSkill(text: String): Skill? {
        val lowerText = text.lowercase()
        // Simple keyword matching (can be improved with fuzzy search)
        return skills.find { skill ->
            skill.keywords.any { keyword -> lowerText.contains(keyword) }
        }
    }
}