<template>
  <div class="common-layout">
    <el-container>
      <el-header style="background-color:#77BBDD; position: relative; font-size: 35px; height: 40px;">
        Address Book
      </el-header>
      <el-main>

      <el-dialog
        v-model="dialogVisible"
        title="Add a new contact"
        width="30%"
        :before-close="handleClose"
      >
        <span>
          <el-form
            :label-position="labelPosition"
            label-width="120px"
            :model="form"
            style="max-width: 500px"
          >
            <el-form-item label="Name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="TelephoneNumber">
              <el-input v-model="form.telephone_number" />
            </el-form-item>
            <el-form-item label="Favor">
              <el-select v-model="form.favor" placeholder="Select">
                <el-option label="Yes" value='Star' />
                <el-option label="No" value='' />
              </el-select>
            </el-form-item>
          </el-form>
        </span>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="save">
              Save
            </el-button>
          </span>
        </template>
      </el-dialog>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="name" label="Name"/>
        <el-table-column prop="telephone_number" label="TelephoneNumber" />
        <el-table-column fixed="right" label="Operations" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="Edit(scope.row)">Edit</el-button>
            <el-popconfirm title="Are you sure you want to delete this contact?" @confirm="Delete(scope.row.id)">
              <template #reference>
              <el-button link type="primary" size="small">Delete</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
    
    <el-footer>
      <div>
        <el-button @click="add" type="primary">
          Add a new contact 
        </el-button>
      </div>
    </el-footer>
    </el-container>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'HomeView',
  data(){
    return{
      form:{},
      dialogVisible:false,
      tableData:[]
    }
  },
  created(){
    this.load()
  },
  methods:{
    add(){
      this.dialogVisible=true
    },
    load(){
      request.get("http://localhost:9090/user").then(
        res=>{
          this.tableData=res
        }
      )
    },
    save(){
      request.post("http://localhost:9090/user",this.form).then(
        res=>{
          this.load()
        }
      )
      this.dialogVisible=false
    },
    Edit(row){
      this.form=JSON.parse(JSON.stringify(row))
      this.dialogVisible=true
    },
    Delete(id){
      request.delete("http://localhost:9090/user/"+id).then(
        res=>{
          this.load()
        }
      )
    },
  }

}
</script>
