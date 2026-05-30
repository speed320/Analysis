import { createRouter, createWebHistory } from 'vue-router'
import InputParameters from '../views/InputParameters.vue'


import App from '../App.vue'
import Register from "@/views/Register.vue";
import Login from "@/views/Login.vue";
import Analyse from "@/views/Analyse.vue";


const routes = [
    // { path: '/', component: App },
    { path: '/parameters', component: InputParameters },
    { path: '/analysis', component: Analyse },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router