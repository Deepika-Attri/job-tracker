package com.example.jobtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 0 = "new", Room assigns a real id
    val company: String,
    val role: String,
    val link: String = "",
    val status: JobStatus = JobStatus.APPLIED,
    val dateApplied: Long = System.currentTimeMillis(), // stored as milliseconds
    val notes: String = ""
)