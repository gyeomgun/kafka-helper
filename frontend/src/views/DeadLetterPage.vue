<template>
  <div>
    <Card shadow>
      <Form ref="formInline" :model="formInline" :label-width="80" inline>
        <FormItem label="Topic">
            <Select v-model="formInline.topic" placeholder="Select topic" @on-change="topicSelect">
              <Option v-for="item in topicList" :value="item.name" :key="item.name">{{ item.name }}</Option>
            </Select>
        </FormItem>
        <FormItem label="Service">
            <Select v-model="formInline.service" placeholder="Select service name">
              <Option v-for="item in hashkeyData" :value="item.hashkey" :key="item.hashkey">{{ item.name }}</Option>
            </Select>
        </FormItem>
        <FormItem label="MessageId">
            <Input v-model="formInline.messageId" placeholder="Enter message id"></Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="search(1)">Search</Button>
        </FormItem>
      </Form>
    </Card>
    <Divider/>
    <Card shadow>
      <Table :columns="deadletterColumn" :data="deadletterData">
        <template slot-scope="{ row }" slot="action">
          <Button type="primary" size="small" style="margin-right: 5px" @click="show(row)">MESSAGE</Button>
          <Button type="primary" size="small" style="margin-right: 5px" @click="show(row)">REASON</Button>
          <Button type="success" size="small" style="margin-right: 5px" @click="show(row)">RE-PUB</Button>
          <Button type="error" size="small" @click="deleteTopic(row.name)">DELETE</Button>
        </template>
      </Table>
      <Page :total="100" show-total />
    </Card>
    <Modal v-model="messagePopup">
      <vue-json-pretty
        :data="messageJson">
      </vue-json-pretty>
    </Modal>
  </div>
</template>
<script>
import VueJsonPretty from 'vue-json-pretty'
import 'vue-json-pretty/lib/styles.css';
  export default {
    components: {
      VueJsonPretty
    },
      data () {                       
          return {
            formInline: {
              topic: '',
              service: '',
              messageId: ''
            },
            messagePopup: false,
            messageJson: '',
            selectedTopic: '',
            topicList: [],
            hashkeyData: [],
            deadletterData: [],
            deadletterColumn: [
              {
                title: 'MessageId',
                key: 'messageId',
                fixed: 'left'
              },
              {
                title: 'Topic',
                key: 'topic'
              },
              {
                title: 'Service',
                key: 'serviceName'
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
                width: 350,
                align: 'center',
                fixed: 'right'
              }
            ]
          }
      },
      created () {
        this.getTopicList()
      },
      methods: {
        topicSelect (select) {
          this.selectedTopic = select
          this.getGroupList()
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
            this.topicList = res.data.info
          })
          .catch((err) => {
            this.$Notice.error({
              title: 'Error',
              desc: 'Could not retrieve topic list.' + err
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
        show (row) {
          this.messageJson = JSON.parse(row.messageJson)
          this.messagePopup = true
        },        
        search (page) {
          this.$api.get(`/api/deadletter?page=${page}`)
            .then((res) => {
              if (res.data.returnCode !== '0000') {
                this.$Notice.error({
                  title: 'Error',
                  desc: 'Could not retrieve deadletter list.' + res.data.returnMessage
                });
                return
              }
              this.deadletterData = res.data.info
            })
            .catch((err) => {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve deadletter list.' + err
            });
          })
        }
      }
  }
</script>