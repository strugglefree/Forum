import { defineStore} from "pinia";
import axios from "axios";

export const useStore = defineStore("general",{
    state: () => {
        return {
            user: {
                id: -1,
                username: '',
                email: '',
                role: '',
                registerTime: null,
                avatar: ''
            },
            forum:{
                types:[]
            }
        }
    },
    getters: {
        avatarUrl(){
            if(this.user.avatar){
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            }else
                return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        }
    },
    actions: {
        findTypeById(id) {
            for (let type of this.forum.types) {
                if (type.id === id)
                    return type
            }
        },
    },
    persist: {
        enabled: true, // 让 Pinia 自动存储数据
        strategies: [
            {
                key: "user-store", // 存储的 key 名称
                storage: localStorage, // 持久化到 localStorage
            }
        ]
    }
})