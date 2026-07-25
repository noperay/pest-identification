<template>
    <div class="indexContainer flex col">
        <div class="flex headContainer">
            <div class="head z-card">
                <div class="card-content">
                    <img :src="userPic" class="card-icon" />
                    <div class="card-text">
                        <div class="card-title">注册用户</div>
                        <div class="card-value">{{ userCount }}人</div>
                    </div>
                </div>
            </div>
            <div class="head1 z-card">
                <div class="card-content">
                    <img :src="articlePic" class="card-icon" />
                    <div class="card-text">
                        <div class="card-title">文章数量</div>
                        <div class="card-value">{{ articleCount }}篇</div>
                    </div>
                </div>
            </div>
            <div class="head1 z-card">
                <div class="card-content">
                    <img :src="detectPic" class="card-icon" />
                    <div class="card-text">
                        <div class="card-title">已检测</div>
                        <div class="card-value">{{ detectCont }}张</div>
                    </div>
                </div>
            </div>
            <div class="head1 z-card">
                <div class="card-content">
                    <img :src="messagePic" class="card-icon" />
                    <div class="card-text">
                        <div class="card-title">频道消息</div>
                        <div class="card-value">{{ messageCount }}条</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="flex midContainer col">
            <div class="flex content1 z-card">
                <div ref="pieChart" style="width: 100%; height: 100%;"></div>
            </div>
            <div class="flex content2 z-card">
                <div ref="lineChart" style="width: 100%; height: 100%;"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { getUsersService } from '@/api/user';
import { articleInfoService } from '@/api/article'
import { getAllDetectService } from '@/api/detection';
import { getUserNameByIdService } from '@/api/user';
import * as echarts from 'echarts';
import userPic from '@/assets/用户.png';
import articlePic from '@/assets/文章.png';
import detectPic from '@/assets/图片.png';
import messagePic from '@/assets/消息.png';
import { getAllMessagesHistory } from '@/api/message'
const chatList = ref([]);
const userCount = ref(0);
const articleCount = ref(0);
const detectCont = ref(0);
const messageCount = ref(0);
const detectList = ref([]);
const pieChart = ref(null);
const lineChart = ref(null);

// 获取所有用户信息 
const getAllUsers = async () => {
    const result = await getUsersService();
    userCount.value = result.data.length;
};

// 获取文章信息 
const getArticleInfo = async () => {
    const result = await articleInfoService();
    articleCount.value = result.data.length;
}

// 获取检测列表 
const getdetectList = async () => {
    let result = await getAllDetectService();
    for (let i = 0; i < result.data.length; i++) {
        let message = result.data[i];
        let usernameResult = await getUserNameByIdService(message.userId);
        message.username = usernameResult.data;
    }
    detectList.value = result.data;
    detectCont.value = result.data.length;
};
// 获取聊天列表
const getchatList = async () => {
    let result = await getAllMessagesHistory();
    for (let i = 0; i < result.data.length; i++) {
        let message = result.data[i];
        let usernameResult = await getUserNameByIdService(message.userId);
        message.username = usernameResult.data;
    }
    chatList.value = result.data;
    messageCount.value = result.data.length;
};
// 初始化饼图 
const initPieChart = () => {
    if (!pieChart.value) return;

    // 统计不同种类的数量 
    const sortCount = {};
    detectList.value.forEach(item => {
        const sort = item.sort || '未分类';
        sortCount[sort] = (sortCount[sort] || 0) + 1;
    });

    // 转换为ECharts需要的格式 
    const pieData = Object.entries(sortCount).map(([name, value]) => ({
        name,
        value
    }));

    // 按数量降序排序 
    pieData.sort((a, b) => b.value - a.value);

    const chart = echarts.init(pieChart.value);
    const option = {
        title: {
            text: '害虫种类识别占比',
            left: 'center',
            top: 20,
            textStyle: {
                fontSize: 18,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            right: 100,
            top: 'center',
            data: pieData.map(item => item.name)
        },
        series: [
            {
                name: '检测数量',
                type: 'pie',
                top: 30,
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: true,
                    formatter: '{b}: {c} ({d}%)'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: '18',
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: true
                },
                data: pieData
            }
        ],
        color: [
            '#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE',
            '#3BA272', '#FC8452', '#9A60B4', '#EA7CCC', '#FF9F7F'
        ]
    };
    chart.setOption(option);

    // 响应式调整 
    window.addEventListener('resize', function () {
        chart.resize();
    });
};

// 初始化折线图 
const initLineChart = () => {
    if (!lineChart.value || !detectList.value.length) return;

    // 计算每种昆虫的平均置信度 
    const confidenceData = {};
    const countData = {};

    detectList.value.forEach(item => {
        const sort = item.sort || '未分类';
        const confidence = parseFloat(item.confidenceLevel) || 0;

        if (!confidenceData[sort]) {
            confidenceData[sort] = 0;
            countData[sort] = 0;
        }

        confidenceData[sort] += confidence;
        countData[sort]++;
    });

    // 计算平均置信度 
    const averageConfidence = {};
    Object.keys(confidenceData).forEach(sort => {
        averageConfidence[sort] = (confidenceData[sort] / countData[sort] * 100).toFixed(2);
    });

    // 转换为数组并按置信度排序 
    const lineData = Object.entries(averageConfidence)
        .map(([name, value]) => ({ name, value: parseFloat(value) }))
        .sort((a, b) => b.value - a.value);

    const chart = echarts.init(lineChart.value);
    const option = {
        title: {
            text: '害虫种类平均置信度',
            left: 'center',
            top: 20,
            textStyle: {
                fontSize: 18,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'axis',
            formatter: '{b}: {c}%'
        },
        xAxis: {
            type: 'category',
            data: lineData.map(item => item.name),
            axisLabel: {
                interval: 0,
                rotate: 30 // 如果名称太长可以旋转 
            }
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: 100,
            axisLabel: {
                formatter: '{value}%'
            }
        },
        series: [
            {
                name: '平均置信度',
                type: 'line',
                data: lineData.map(item => item.value),
                smooth: true,
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: {
                    color: '#5470C6'
                },
                lineStyle: {
                    width: 3
                },
                label: {
                    show: true,
                    formatter: '{c}%',
                    position: 'top'
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(84, 112, 198, 0.5)' },
                        { offset: 1, color: 'rgba(84, 112, 198, 0.1)' }
                    ])
                }
            }
        ],
        grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            top: '20%',
            containLabel: true
        }
    };
    chart.setOption(option);

    // 响应式调整 
    window.addEventListener('resize', function () {
        chart.resize();
    });
};

// 监听detectList变化，更新图表 
watch(detectList, () => {
    initPieChart();
    initLineChart();
}, { deep: true });

// 初始化 
onMounted(async () => {
    await getAllUsers();
    await getArticleInfo();
    await getdetectList();
    await getchatList();
});
</script>

<style scoped>
.indexContainer {
    /* border: 1px solid red; */
    height: 100%;
    width: 100%;
    /* padding: 20px; */
    padding-left: 20px;
    padding-right: 20px;
    box-sizing: border-box;
}

.headContainer {

    height: 150px;
    width: 100%;
    justify-content: space-between;
    gap: 20px;
}

.head,
.head1 {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 300px;
    height: 100%;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
}

.head:hover,
.head1:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.card-content {
    /* border: 1px solid red; */
    display: flex;
    align-items: center;
    width: 100%;
    height: 100px;
    padding: 0 20px;
}

.card-icon {
    width: 50px;
    height: 50px;
    margin-right: 15px;
}

.card-text {
    flex: 1;
}

.card-title {
    font-size: 16px;
    color: #666;
    margin-bottom: 5px;
}

.card-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}

.midContainer {
    margin-top: 20px;
    width: 100%;
    justify-content: center;
    gap: 20px;
}

.content1,
.content2 {
    width: 100%;
    height: 400px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    padding: 10px;
    box-sizing: border-box;
}

.z-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.flex {
    display: flex;
}

.col {
    flex-direction: column;
}
</style>