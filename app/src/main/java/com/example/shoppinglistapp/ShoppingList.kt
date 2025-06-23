package com.example.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

data class ShoppingItem(
    var id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp(){

    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false)}
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    val mainBackgroundColor = Color(0xFFD7FDD7)
    val buttonGreen = Color.Green
    val textFieldFocusedBorderColor = buttonGreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackgroundColor)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .height(50.dp)
                .fillMaxWidth(0.6f)
        ) {
            Text("Add Item", fontSize = 16.sp)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItems){
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(
                        item = item,
                        onEditComplete = {
                            editedName, editedQuantity ->
                            sItems = sItems.map{ it.copy(isEditing = false)}
                            val editedItem = sItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        },
                        buttonGreen
                    )
                }
                else {
                    ShoppingListItem(
                        item = item,
                        onEditClick = {
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id)}
                        },
                        onDeleteClick = {
                            sItems = sItems - item
                        },
                        buttonGreen
                    )
                }
            }
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false},
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if(itemName.isNotBlank()){
                                val newItem = ShoppingItem(
                                    id = sItems.size+1,
                                    name = itemName,
                                    quantity = itemQuantity.toInt()
                                )
                                sItems = sItems + newItem
                                showDialog = false
                                itemName = ""
                                itemQuantity = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(45.dp)
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = {
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(45.dp)
                    ){
                        Text("Cancel")
                    }
                }
            },
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Add Shopping Item", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = textFieldFocusedBorderColor,
                            focusedLabelColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        label = { Text("Quantity") },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = textFieldFocusedBorderColor,
                            focusedLabelColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            },
            containerColor = Color(0xFFE0F2F1),
            shape = RoundedCornerShape(16.dp),
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit, buttonGreen: Color){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.weight(1f)){
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(8.dp)
            )
        }
        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(45.dp)
        ){
            Text("Save")
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    buttonGreen: Color
){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, buttonGreen),
                shape = RoundedCornerShape(20)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = item.name,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Qty: ${item.quantity}",
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Row(modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick){
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
            }
            IconButton(onClick = onDeleteClick){
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black)
            }
        }

    }
}