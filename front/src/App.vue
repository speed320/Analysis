<script>
import {defineComponent, onMounted} from "vue";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {useRouter} from "vue-router";

  export default defineComponent({
    components: {Footer, Header},
    setup(){
      const router = useRouter()
      onMounted(()=>{
        if(localStorage.getItem('token')){
          router.push('/parameters')
        }
        else{
          router.push('/login')
        }
      });
    }
  })

</script>

<template>
  <Header></Header>
  <main class="main">
  <router-view></router-view>
  </main>
  <Footer></Footer>
</template>

<style>
  *{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  #app {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  .main{
    margin-bottom: 60px;
    margin-top: 50px;
  }
</style>