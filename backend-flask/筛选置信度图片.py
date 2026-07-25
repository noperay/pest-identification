import os
from ultralytics import YOLO
from PIL import Image

# 加载YOLOv8模型
model = YOLO('模型/50+120/weights/best.pt')

# 定义输入和输出文件夹
input_folder = 'images'
output_folder = 'label_yolo'

# 创建输出文件夹（如果不存在）
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# 遍历输入文件夹中的所有图片
for filename in os.listdir(input_folder):
    if filename.endswith(('.png', '.jpg', '.jpeg')):
        image_path = os.path.join(input_folder, filename)
        # 使用模型进行预测
        results = model(image_path)

        # 检查是否有置信度大于0.85的检测结果
        high_confidence = False
        for result in results:
            boxes = result.boxes
            for box in boxes:
                confidence = box.conf.item()
                if confidence > 0.85:
                    high_confidence = True
                    break
            if high_confidence:
                break

        # 如果有高置信度检测结果，将图片复制到输出文件夹
        if high_confidence:
            img = Image.open(image_path)
            output_path = os.path.join(output_folder, filename)
            img.save(output_path)
            print(f'Saved {filename} to {output_folder}')