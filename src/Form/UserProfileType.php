<?php

namespace App\Form;

use App\Entity\User;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class UserProfileType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('firstName', TextType::class, [
                'label' => 'First name',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('lastName', TextType::class, [
                'label' => 'Last name',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('phone', TextType::class, [
                'required' => false,
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('avatarUrl', TextType::class, [
                'label' => 'Avatar URL',
                'required' => false,
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('description', TextareaType::class, [
                'required' => false,
                'attr' => ['class' => 'form-control', 'rows' => 4],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('address', TextType::class, [
                'required' => false,
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('city', TextType::class, [
                'required' => false,
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('country', TextType::class, [
                'required' => false,
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => User::class,
        ]);
    }
}
