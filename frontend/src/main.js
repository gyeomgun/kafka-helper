import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ViewUI from 'view-design';
import 'view-design/dist/styles/iview.css';
import locale from 'view-design/dist/locale/en-US';
import axios from 'axios'
import underscore from 'vue-underscore'
import VueJsonPretty from 'vue-json-pretty'
import 'vue-json-pretty/lib/styles.css'

Vue.use(VueJsonPretty)
Vue.use(underscore)
Vue.use(ViewUI, { locale })
Vue.prototype.$api = axios
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
