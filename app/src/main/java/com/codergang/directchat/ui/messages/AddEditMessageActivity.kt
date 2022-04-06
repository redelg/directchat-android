package com.codergang.directchat.ui.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.codergang.directchat.R
import com.codergang.directchat.data.entity.MessageDB
import com.codergang.directchat.databinding.ActivityAddEditMessageBinding
import com.codergang.directchat.ui.util.showSnackBar

class AddEditMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditMessageBinding
    private val viewModel: MessagesViewModel by viewModels()
    private var messageId = 0

    private lateinit var messageData: MessageDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageId = intent.getIntExtra("messageId", 0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setup()
        setObservers()
    }

    private fun setup() {
        if (messageId == 0) {
            title = getString(R.string.text_new_message)
            binding.btnSave.setOnClickListener {
                saveMessage()
            }
        } else {
            title = getString(R.string.text_edit_message)
            binding.btnSave.setOnClickListener {
                editMessage()
            }
            viewModel.getMessage(messageId)
        }
    }

    private fun setObservers() {
        viewModel.message.observe(this) {
            messageData = it
            binding.etContent.setText(it.content)
            binding.etTitle.setText(it.title)
        }
        viewModel.saved.observe(this) {
            finish()
        }
        viewModel.deleted.observe(this) {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (messageId != 0) {
            menuInflater.inflate(R.menu.menu_delete, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteMessage(messageData)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun saveMessage() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()

        when {
            title.isNullOrEmpty() -> {
                showSnackBar(binding.root, getString(R.string.text_enter_title))
                return
            }
            content.isNullOrEmpty() -> {
                showSnackBar(binding.root, getString(R.string.text_enter_your_message))
                return
            }
        }

        viewModel.saveMessage(MessageDB(0, title, content))
    }

    private fun editMessage() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()

        when {
            title.isNullOrEmpty() -> {
                showSnackBar(binding.root, getString(R.string.text_enter_title))
                return
            }
            content.isNullOrEmpty() -> {
                showSnackBar(binding.root, getString(R.string.text_enter_your_message))
                return
            }
        }

        messageData.title = title
        messageData.content = content

        viewModel.editMessage(messageData)
    }

}