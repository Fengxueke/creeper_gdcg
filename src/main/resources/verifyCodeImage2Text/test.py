import ddddocr

ocr = ddddocr.DdddOcr(show_ad=False)
with open("src/main/resources/verifyCodeImage2Text/1.jfif", "rb") as f:
    img_bytes = f.read()
res = ocr.classification(img_bytes)
print(res)
