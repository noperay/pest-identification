from flask import Flask, request, jsonify, render_template
import requests
from ultralytics import YOLO
from PIL import Image
from io import BytesIO
from result import Result  # 导入 Result 类
from oss_util import upload_image_to_oss  # 导入 OSS 上传功能

# Flask 应用初始化
app = Flask(__name__)

# YOLOv11 模型路径
MODEL_PATH = '模型/50+120/best.pt'

# 加载 YOLOv11 模型
try:
    model = YOLO(MODEL_PATH)
except Exception as e:
    print(f"加载失败: {e}")
    model = None

# 英文类别到中文类别的字典
english_to_chinese = {
    'rice leaf roller': '稻纵卷叶螟',
    'rice leaf caterpillar': '稻叶螟',
    'paddy stem maggot': '稻茎蛆',
    'asiatic rice borer': '亚洲玉米螟',
    'yellow rice borer': '二化螟',
    'rice gall midge': '稻瘿蚊',
    'Rice Stemfly': '稻秆蝇',
    'brown plant hopper': '褐飞虱',
    'white backed plant hopper': '白背飞虱',
    'small brown plant hopper': '灰飞虱',
    'rice water weevil': '稻水象甲',
    'rice leafhopper': '稻叶蝉',
    'grain spreader thrips': '稻蓟马',
    'rice shell pest': '稻壳虫',
    'grub': '蛴螬',
    'mole cricket': '蝼蛄',
    'wireworm': '金针虫',
    'white margined moth': '白缘螟蛾',
    'black cutworm': '小地老虎',
    'large cutworm': '大地老虎',
    'yellow cutworm': '黄地老虎',
    'red spider': '红蜘蛛',
    'corn borer': '玉米螟',
    'army worm': '粘虫',
    'aphids': '蚜虫',
    'Potosiabre vitarsis': '琉璃弧丽金龟',
    'peach borer': '桃蛀螟',
    'english grain aphid': '麦长管蚜',
    'green bug': '麦二叉蚜',
    'bird cherry-oataphid': '禾谷缢管蚜',
    'wheat blossom midge': '小麦吸浆虫',
    'penthaleus major': '麦圆蜘蛛',
    'longlegged spider mite': '长腿红蜘蛛',
    'wheat phloeothrips': '小麦管蓟马',
    'wheat sawfly': '小麦叶蜂',
    'cerodonta denticornis': '麦鞘毛眼水蝇',
    'beet fly': '甜菜潜叶蝇',
    'flea beetle': '跳甲',
    'cabbage army worm': '甘蓝夜蛾',
    'beet army worm': '甜菜夜蛾',
    'Beet spot flies': '甜菜斑蝇',
    'meadow moth': '草地螟',
    'beet weevil': '甜菜象甲',
    'sericaorient alismots chulsky': '东方绢金龟',
    'alfalfa weevil': '苜蓿叶象甲',
    'flax budworm': '亚麻夜蛾',
    'alfalfa plant bug': '苜蓿盲蝽',
    'tarnished plant bug': '茶翅蝽',
    'Locustoidea': '蝗虫',
    'lytta polita': '绿芫菁',
    'legume blister beetle': '豆芫菁',
    'blister beetle': '斑蝥',
    'therioaphis maculata Buckton': '苜蓿斑蚜',
    'odontothrips loti': '苜蓿蓟马',
    'Thrips': '蓟马',
    'alfalfa seed chalcid': '苜蓿籽蜂',
    'Pieris canidia': '东方菜粉蝶',
    'Apolygus lucorum': '绿盲蝽',
    'Limacodidae': '刺蛾科',
    'Viteus vitifoliae': '葡萄根瘤蚜',
    'Colomerus vitis': '葡萄瘿螨',
    'Brevipoalpus lewisi McGregor': '刘氏短须螨',
    'oides decempunctata': '十点叶甲',
    'Polyphagotars onemus latus': '侧多食跗线螨',
    'Pseudococcus comstocki Kuwana': '康氏粉蚧',
    'parathrene regalis': '葡萄透翅蛾',
    'Ampelophaga': '葡萄天蛾',
    'Lycorma delicatula': '斑衣蜡蝉',
    'Xylotrechus': '天牛',
    'Cicadella viridis': '大青叶蝉',
    'Miridae': '盲蝽科',
    'Trialeurodes vaporariorum': '温室白粉虱',
    'Erythroneura apicalis': '葡萄二星叶蝉',
    'Papilio xuthus': '柑橘凤蝶',
    'Panonchus citri McGregor': '柑橘全爪螨',
    'Phyllocoptes oleiverus ashmead': '柑橘锈螨',
    'Icerya purchasi Maskell': '吹绵蚧',
    'Unaspis yanonensis': '矢尖蚧',
    'Ceroplastes rubens': '红蜡蚧',
    'Chrysomphalus aonidum': '红圆蚧',
    'Parlatoria zizyphus Lucus': '椰圆蚧',
    'Nipaecoccus vastalor': '堆蜡粉蚧',
    'Aleurocanthus spiniferus': '黑刺粉虱',
    'Tetradacus c Bactrocera minax ': '柑橘大实蝇',
    'Dacus dorsalis(Hendel)': '橘小实蝇',
    'Bactrocera tsuneonis': '日本蜜柑蝇',
    'Prodenia litura': '斜纹夜蛾',
    'Adristyrannus': '安氏突颜蝗',
    'Phyllocnistis citrella Stainton': '柑橘潜叶蛾',
    'Toxoptera citricidus': '橘蚜',
    'Toxoptera aurantii': '桔二叉蚜',
    'Aphis citricola Vander Goot': '绣线菊蚜',
    'Scirtothrips dorsalis Hood': '茶黄蓟马',
    'Dasineura sp': '瘿蚊属',
    'Lawana imitata Melichar': '龙眼鸡',
    'Salurnis marginella Guerr': '柿广翅蜡蝉',
    'Deporaus marginatus Pascoe': '芒果切叶象甲',
    'Chlumetia transversa': '芒果横线尾夜蛾',
    'Mango flat beak leafhopper': '芒果扁喙叶蝉',
    'Rhytidodera bowrinii white': '芒果脊胸天牛',
    'Sternochetus frigidus': '杧果核象甲',
    'Cicadellidae': '叶蝉科'
}


# 路由：主页
@app.route('/')
def index():
    return render_template('index.html')


# 路由：通过图片 URL 进行检测
@app.route('/detect', methods=['POST'])
def detect_url():
    data = request.get_json()

    if 'url' not in data:
        return jsonify(Result.error(" 没有找到图片 URL").to_dict())

    image_url = data['url']

    try:
        if not model:
            return jsonify(Result.error(" 模型加载失败，无法进行检测").to_dict())

        # 通过 URL 下载图片
        response = requests.get(image_url)
        if response.status_code != 200:
            return jsonify(Result.error(f" 无法下载图片，HTTP状态码: {response.status_code}").to_dict())

        # 将下载的图片转换为 PIL Image 对象
        img = Image.open(BytesIO(response.content))

        results = model(img)

        detected_info_list = []
        for r in results:
            boxes = r.boxes
            for box in boxes:
                confidence = float(box.conf[0])
                class_id = int(box.cls[0])
                class_name = r.names[class_id]
                # 将英文类别名称转换为中文
                chinese_name = english_to_chinese.get(class_name, class_name)
                confidence_str = "{:.3f}".format(confidence)
                detected_info_list.append({
                    "sort": chinese_name,
                    "confidenceLevel": confidence_str
                })

        if not detected_info_list:
            detected_info_list.append({
                "sort": "未检测到任何类别",
                "confidenceLevel": "0.000"
            })

        # 获取绘制结果的图片
        result_img = Image.fromarray(results[0].plot())

        # 将结果图片保存到内存中
        img_byte_arr = BytesIO()
        result_img.save(img_byte_arr, format='JPEG')
        img_byte_arr.seek(0)  # 需要将流的游标回到开头

        # 上传到阿里云 OSS（按日期创建文件夹）
        oss_file_url = upload_image_to_oss(img_byte_arr.getvalue())

        combined_data = {
            "imgUrl": oss_file_url,
            "result": detected_info_list
        }
        return jsonify(Result.success(combined_data).to_dict())
    except Exception as e:
        return jsonify(Result.error(f" 检测失败: {str(e)}").to_dict())


if __name__ == '__main__':
    app.run(debug=False, port=5000)
