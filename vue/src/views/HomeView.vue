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
            <el-form-item label="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="Address">
              <el-input v-model="form.address" />
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
        <el-table-column fixed="left" label="favor" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.favor === 'Star' ? 'success' : 'info'">
              {{ scope.row.favor }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Name"/>
        <el-table-column prop="telephone_number" label="TelephoneNumber" />
        <el-table-column prop="email" label="email" />
        <el-table-column prop="address" label="Address" />
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
        <el-button @click="exportExcel" type="success">
          Export to Excel
        </el-button>
        <el-button type="primary" @click="selectFile">
          Import from Excel
        </el-button>
        <input type="file" ref="fileInput" @change="handleFileUpload" accept=".xlsx, .xls" style="display: none;" />
      </div>
    </el-footer>
    </el-container>
  </div>
</template>

<script>
import request from '@/utils/request'
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';

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
    selectFile() {
      this.$refs.fileInput.click();
    },
    exportExcel() {
      const tableData = this.tableData;
      const ws = XLSX.utils.json_to_sheet(tableData);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, 'Contacts');
      const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
      saveAs(blob, 'contacts.xlsx');
    },
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.readExcel(file);
      }
    },
    readExcel(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const data = e.target.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const json = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        const headers = json[0];
        for (let i = 1; i < json.length; i++) {
          const row = json[i];
          if (row) {
            const obj = row.reduce((obj, value, key) => {
              obj[headers[key]] = value;
              return obj;
            }, {});
            
            request.post("http://localhost:9090/user",obj).then(
              res=>{
                this.load()
              }
            )
          }
        }
      };
      reader.readAsBinaryString(file);
    }
  }

}
</script>
