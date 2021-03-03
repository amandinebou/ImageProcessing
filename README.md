### **Image processing application in JavaFX** 

**Authors :** Eloïse Zaghrini, Amandine Bouguessa, Armelle Coutaux

This application allows you to process an image in .png or .jpg format with different options.
Each operation can be carried out successively

Import an image with background (Seam Carving does not work on images without backgrounds) and get started!

Once you've done this, save your image (it will be in .png format).

Here are the different operations available :

- **Recropping the image (cropping)**

In this feature, the user can crop the image to the left, right, top or bottom using sliders.
The values given to the left/top sliders are the number of pixels that will be trimmed to the left/top of the image.
The image size (width/height) minus the values given to the right/bottom sliders are the number of pixels to be cropped right/bottom.

Select the desired size and then press "Image Processing".

- **Scaling**

In this feature, the image is flattened to the desired size while maintaining its integrity.

Select the desired width and height and press "Image Processing".

- **Seam Carving**

In this feature, the user can resize his image by using the Seam Carving feature, decreasing or increasing its dimensions. Seam Carving is based on the calculation of the pixel energies of the image (in standard 1 or 2 at the user's choice), and then on the removal/duplication of the seams of pixels with the lowest energy. This treatment allows the image to be resized while keeping as much information as possible!

Choose the desired width and height and press "Image processing".

- **Drawing interface**

Allows you to draw on the image.
Choose the desired colour using the three Blue/Red/Green sliders (dynamic display in the colour rectangle) and choose the desired brush thickness.

Draw what you want!

- **Undo/Redo**

Did you make a mistake? No problem! Go back to your previous actions, you can also go back to the next action.A





Translated with www.DeepL.com/Translator (free version)
