<template>
  <div>
    <Card shadow>
      <AutoComplete
          v-model="selectedTopic"
          :data="topicList"
          :filter-method="filterMethod"
          @on-change="topicSelect"
          placeholder="input topic name"
          style="width:500px">
      </AutoComplete>
    </Card>
    <Divider/>
    <Card shadow>
      <h2>Hashkey List</h2>
      <Button type="primary" @click="showDrawer">Registration</Button>
      <Table :columns="hashkeyColumn" :data="hashkeyData" @on-row-click="serviceSelect">
        <template slot-scope="{ row }" slot="action">
            <Button type="error" size="small" @click="deleteGroup(row)">Delete</Button>
        </template>
      </Table>
    </Card>
    <Divider/>
    <Card shadow>
      <h2>Connected Host List [{{this.selectedServiceName}}]</h2>
      <Table :columns="hostColumn" :data="hostData">
        <template slot-scope="{ row, index }" slot="action">
            <Button type="success" size="small" style="margin-right: 5px" @click="show(index)">ON</Button>
            <Button type="error" size="small" @click="deleteTopic(row.name)">OFF</Button>
        </template>
      </Table>
    </Card>
    <Drawer
      title="Registration GroupId"
      v-model="isShowDrawer"
      width="720"
      :mask-closable="false"
      :styles="styles">
      <Form :model="formData">
        <Row :gutter="32">
              <Col span="24">
                  <FormItem label="Hashkey" label-position="top">
                      <Input search enter-button="Generate" v-model="formData.hashkey" @on-search="generate" />
                  </FormItem>
              </Col>
          </Row>
          <Row :gutter="32">
              <Col span="12">
                  <FormItem label="Service name" label-position="top">
                      <Input v-model="formData.name" placeholder="please enter consumer service name" />
                  </FormItem>
              </Col>
              <Col span="12">
                  <FormItem label="GroupId" label-position="top">
                      <Input v-model="formData.groupId" placeholder="please enter consumer groupId" />
                  </FormItem>
              </Col>              
          </Row>
          <FormItem label="Description" label-position="top">
              <Input type="textarea" v-model="formData.desc" :rows="4" placeholder="please enter the description" />
          </FormItem>
      </Form>
      <div class="demo-drawer-footer">
          <Button style="margin-right: 8px" @click="isShowDrawer = false">Cancel</Button>
          <Button type="primary" @click="add">Submit</Button>
      </div>
    </Drawer>     
  </div>
</template>
<script>
  import {_} from 'vue-underscore'
  export default {
      data () {                       
          return {
            timer: null,
            selectedTopic: '',
            selectedServiceName: '',
            topicList: [],
            hashkeyData: [],
            hostData: [],
            isShowDrawer: false,
            styles: {
              height: 'calc(100% - 55px)',
              overflow: 'auto',
              paddingBottom: '53px',
              position: 'static'
            },
            formData: {
              hashkey: '',
              name: '',
              groupId: '',
              desc: ''
            },
            hashkeyColumn: [
              {
                title: 'Name',
                key: 'name'
              },
              {
                title: 'Hashkey',
                key: 'hashkey'
              },
              {
                title: 'GroupId',
                key: 'groupId'
              },
              {
                title: 'created At',
                key: 'createdAt'
              },              
              {
                title: 'Action',
                slot: 'action',
                width: 150,
                align: 'center'
              }
            ],
            hostColumn: [
              {
                title: 'Name',
                key: 'hostName'
              },
              {
                title: 'Status',
                key: 'status'
              },
              {
                title: 'updated At',
                key: 'updatedAt'
              },              
              {
                title: 'Action',
                slot: 'action',
                width: 150,
                align: 'center'
              }
            ]
          }
      },
      created () {
        this.getTopicList()
      },
      methods: {
        topicSelect () {
          this.getGroupList()
        },
        filterMethod (value, option) {
          return option.toUpperCase().indexOf(value.toUpperCase()) !== -1;
        },
        getTopicList () {
          this.$api.get(`/api/topic`)
          .then((res) => {
            if (res.data.returnCode !== '0000') {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve topic list.' + res.data.returnMessage
              });
              return
            }
            let topicNames = _.map(res.data.info, s => s.name)
            this.topicList = topicNames
          })
          .catch((err) => {
            this.$Notice.error({
              title: 'Error',
              desc: 'Could not retrieve topic list.' + err
          });
          })
        },
        generate () {
          this.$api.get(`/api/consumer/hashkey`)
          .then((res) => {
            if (res.data.returnCode !== '0000') {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve hashkey.' + res.data.returnMessage
              });
              return
            }
            this.formData.hashkey = res.data.info
          })
          .catch((err) => {
            this.$Notice.error({
              title: 'Error',
              desc: 'Could not retrieve hashkey.' + err
          });
          })          
        },
        showDrawer () {
          if (this.selectedTopic === '') {
            this.$Notice.error({
              title: 'Error',
              desc: 'Select topic first.'
            })
            return
          }
          this.formData.hashkey = ''
          this.formData.name = ''
          this.formData.groupId = ''
          this.formData.desc = ''
          this.generate()
          this.isShowDrawer = true
        },
        add () {
          this.$api.post(`/api/consumer/${this.selectedTopic}/group`, this.formData)
          .then((res) => {
            if (res.data.returnCode !== '0000') {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not add hashkey.' + res.data.returnMessage
              });
              return
            }
            
            this.$Notice.success({
                title: 'Success',
                desc: ''
            });

            this.getGroupList()
            this.isShowDrawer = false
          })
          .catch((err) => {
            this.$Notice.error({
              title: 'Error',
              desc: 'Could not add hashkey.' + err
          });
          })             
        },
        getGroupList () {
          this.$api.get(`/api/consumer/${this.selectedTopic}/group`)
            .then((res) => {
              if (res.data.returnCode !== '0000') {
                this.$Notice.error({
                  title: 'Error',
                  desc: 'Could not retrieve group list.' + res.data.returnMessage
                });
                return
              }
              this.hashkeyData = res.data.info
            })
            .catch((err) => {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve group list.' + err
            });
          })
        },
        deleteGroup (row) {
          this.$api.delete(`/api/consumer/group/${row.id}`)
            .then((res) => {
              if (res.data.returnCode !== '0000') {
                this.$Notice.error({
                  title: 'Error',
                  desc: 'Could not delete group.' + res.data.returnMessage
                });
                return
              }
              this.getGroupList()
            })
            .catch((err) => {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not delete group.' + err
            });
          })
        },
        serviceSelect (row) {
          if (this.timer !== null) {
            setInterval(this.timer);
          }
          this.selectedServiceName = row.name
          this.getConnectedHostList(row.hashkey)

          this.timer = setInterval(() => {
            this.getConnectedHostList(row.hashkey)
          }, 3000);

          // this.getConnectedHostList(row.hashkey)
        },
        getConnectedHostList (selectedServiceHashkey) {
          console.log('we')
          this.$api.get(`/api/host?hashkey=${selectedServiceHashkey}`)
            .then((res) => {
              if (res.data.returnCode !== '0000') {
                this.$Notice.error({
                  title: 'Error',
                  desc: 'Could not retrieve host list.' + res.data.returnMessage
                });
                return
              }
              this.hostData = res.data.info
            })
            .catch((err) => {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve host list.' + err
            });
          })          
        }
    }
  }
</script>